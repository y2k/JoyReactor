using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Images
{
    public abstract class BaseImageRequest
    {
        protected abstract Task<object> DecodeImageAsync(byte[] data);

        protected abstract void SetToTarget(object target, object image);

        static readonly OperationTransaction Transaction = new OperationTransaction();
        static readonly DiskCache DiskCache = new DiskCache();
        static BaseMemoryCache MemoryCache;

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
                    MemoryCache = CreateMemoryCache();
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
                            data = await DownloadAsync(uri);
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

        private static async Task<byte[]> DownloadAsync(Uri uri)
        {
            var client = ServiceLocator.Current.GetInstance<WebDownloader>();
            using (var r = await client.ExecuteAsync(uri))
            {
                var buffer = new MemoryStream();
                await r.Stream.CopyToAsync(buffer);
                return buffer.ToArray();
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

        public class ThumbnailUri
        {
            const string ThumbnailDomain = "api-i-twister.net";
            const string ThumbnailTemplate = "https://" + ThumbnailDomain + ":8002/Cache/Get?bgColor=ffffff&maxHeight=500&width={0}&url={1}";
            const string OriginalTemplate = "https://" + ThumbnailDomain + ":8002/Cache/Get?url={1}";

            readonly int maxSize;
            readonly Uri url;
            string format;

            internal ThumbnailUri(Uri url)
                : this(url, -1)
            {
            }

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
                var template = maxSize > 0 ? ThumbnailTemplate : OriginalTemplate;
                var result = string.Format(template, maxSize, Uri.EscapeDataString("" + url));
                if (format != null)
                    result += "&format=" + Uri.EscapeDataString(format);
                return new Uri(result);
            }

            bool IsCanCreateThumbnail()
            {
                return maxSize != 0 && url != null && url.Host != ThumbnailDomain;
            }

            public ThumbnailUri SetFormat(string format)
            {
                this.format = format;
                return this;
            }
        }
    }
}