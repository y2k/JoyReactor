using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using ModernHttpClient;

namespace JoyReactor.Core.Model.Images
{
    public abstract class BaseImageRequest
    {
        protected abstract Task<object> DecodeImageAsync(byte[] data);

        protected abstract void SetToTarget(object target, object image);

        static readonly OperationTransaction Transaction = new OperationTransaction();
        static readonly DiskCache DiskCache = new DiskCache();
        static BaseMemoryCache MemoryCache;
        static HttpClient Downloader;

        string originalUrl;
        int sizePx;

        protected abstract BaseMemoryCache CreateMemoryCache();

        public BaseImageRequest CropIn(int sizePx)
        {
            this.sizePx = sizePx;
            return this;
        }

        public BaseImageRequest SetUri(string originalUrl)
        {
            this.originalUrl = originalUrl;
            return this;
        }

        public async void To(object target)
        {
            lock (DiskCache)
            {
                if (MemoryCache == null)
                {
                    MemoryCache = CreateMemoryCache();
                    Downloader = new HttpClient(new NativeMessageHandler());
                }
            }

            Transaction.Begin(target, this);
            try
            {
                if (originalUrl == null)
                {
                    SetToTarget(target, null);
                    return;
                }

                var uri = new ThumbnailUri(new Uri(originalUrl), sizePx).ToUri();
                var imageFromCache = MemoryCache.Get(uri);
                if (imageFromCache != null)
                {
                    SetToTarget(target, imageFromCache);
                    return;
                }

                SetToTarget(target, null);

                var cachedBytes = await DiskCache.GetAsync(uri);
                if (IsInvalidState())
                    return;
                if (cachedBytes == null)
                {
                    byte[] data = null;
                    for (int n = 0; n < 5; n++)
                    {
                        try
                        {
                            data = await Downloader.GetByteArrayAsync(uri);
                            if (IsInvalidState())
                                return;
                            break;
                        }
                        catch
                        {
                            await Task.Delay(500 << n);
                        }
                    }
                    if (data == null)
                        return;
                    
                    await DiskCache.PutAsync(uri, data);
                    if (IsInvalidState())
                        return;
                    var image = await DecodeImageAsync(data);
                    if (IsInvalidState())
                        return;
                    MemoryCache.Put(uri, image);
                    SetToTarget(target, image);
                }
                else
                {
                    var image = await DecodeImageAsync(cachedBytes);
                    if (IsInvalidState())
                        return;
                    MemoryCache.Put(uri, image);
                    SetToTarget(target, image);
                }
            }
            finally
            {
                Transaction.End(target, this);
            }
        }

        bool IsInvalidState()
        {
            return !Transaction.IsValid(this);
        }

        class OperationTransaction
        {
            readonly Dictionary<object, BaseImageRequest> LockedTargets = new Dictionary<object, BaseImageRequest>();

            internal void Begin(object target, BaseImageRequest requste)
            {
                LockedTargets[target] = requste;
            }

            internal void End(object target, BaseImageRequest requste)
            {
                if (IsValid(requste))
                    LockedTargets.Remove(target);
            }

            internal bool IsValid(BaseImageRequest requste)
            {
                return LockedTargets.ContainsValue(requste);
            }
        }

        class ThumbnailUri
        {
            const string ThumbnailDomain = "api-i-twister.net";
            const string ThumbnailTemplate = "https://" + ThumbnailDomain + ":8002/Cache/Get?bgColor=ffffff&maxHeight=500&width={0}&url={1}";
        
            readonly int maxSize;
            readonly Uri url;

            internal ThumbnailUri(Uri url, int maxSize)
            {
                this.url = url;
                this.maxSize = maxSize;
            }

            internal Uri ToUri()
            {
                return IsCanCreateThumbnail() ? CreateThumbnailUri() : url;
            }

            Uri CreateThumbnailUri()
            {
                return new Uri(string.Format(ThumbnailTemplate, maxSize, Uri.EscapeDataString("" + url)));
            }

            bool IsCanCreateThumbnail()
            {
                return maxSize != 0 && url != null && url.Host != ThumbnailDomain;
            }
        }
    }
}