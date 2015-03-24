using System;
using Microsoft.Practices.ServiceLocation;
using XamarinCommons.Image;

namespace JoyReactor.Core.Model
{
    public class ImageRequest
    {
        const string ThumbnailDomain = "api-i-twister.net";
        const string ThumbnailTemplate = "https://" + ThumbnailDomain + ":8002/Cache/Get?maxHeight=500&width={0}&url={1}";

        readonly static ImageDownloader DownloaderInstance = new ImageDownloader
        {
            Decoder = ServiceLocator.Current.GetInstance<ImageDecoder>(),
            DiskCache = new DefaultDiskCache(),
            MemoryCache = new DefaultMemoryCache(),
        };

        Uri url;
        int maxSize;
        object token = new object();

        public ImageRequest SetToken(object token)
        {
            this.token = token;
            return this;
        }

        public ImageRequest SetUrl(string url)
        {
            SetUrl(url == null ? null : new Uri(url));
            return this;
        }

        public ImageRequest SetUrl(Uri url)
        {
            this.url = url;
            return this;
        }

        public ImageRequest CropIn(int maxSize)
        {
            this.maxSize = maxSize;
            return this;
        }

        public async void Into<T>(Action<T> callback)
        {
            callback(default(T));
            var result = await DownloaderInstance.LoadAsync(token, CreateDownloadUrl());
            if (result != ImageDownloader.InvalideImage)
                callback(ConvertImage<T>(result));
        }

        Uri CreateDownloadUrl()
        {
            return IsCanCreateThumbnail()
                ? new Uri(string.Format(ThumbnailTemplate, maxSize, Uri.EscapeDataString("" + url)))
                : url;
        }

        bool IsCanCreateThumbnail()
        {
            return maxSize != 0 && url != null && url.Host != ThumbnailDomain;
        }

        T ConvertImage<T>(object metaImage)
        {
            var convert = DownloaderInstance.Decoder as IImageConverter;
            return convert == null ? (T)metaImage : convert.Convert<T>(metaImage);
        }

        public interface IImageConverter
        {
            T Convert<T>(object metaImage);
        }
    }
}