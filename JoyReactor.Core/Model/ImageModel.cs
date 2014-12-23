using Microsoft.Practices.ServiceLocation;
using System;
using XamarinCommons.Image;

namespace JoyReactor.Core.Model
{
    public class ImageModel
    {
        ImageDownloader imageDownloader = new ImageDownloader
        {
            Decoder = ServiceLocator.Current.GetInstance<ImageDecoder>(),
            DiskCache = new DefaultDiskCache(),
            MemoryCache = new DefaultMemoryCache(),
        };

        public async void Load(object token, Uri originalUri, int maxWidth, Action<object> callback)
        {
            var thumbnailUrl = CreateThumbnailUrl(originalUri, maxWidth);
            var image = await imageDownloader.LoadAsync(token, thumbnailUrl);
            if (image != ImageDownloader.InvalideImage)
                callback(image);
        }

        public string CreateThumbnailUrl(string url, int px)
        {
            return url == null ? null : "" + CreateThumbnailUrl(new Uri(url), px);
        }

        Uri CreateThumbnailUrl(Uri url, int px)
        {
            if (px == 0)
                return url;

            var s = string.Format(
                        "https://remote-cache.api-i-twister.net/Cache/Get?maxHeight=500&width={0}&url={1}",
                        px, Uri.EscapeDataString("" + url));
            return new Uri(s);
        }
    }
}