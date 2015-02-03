using Microsoft.Practices.ServiceLocation;
using XamarinCommons.Image;
using System;

namespace JoyReactor.Core.Model
{
    public class ImageRequest
    {
        static ImageDownloader imageDownloader = new ImageDownloader
        {
            Decoder = ServiceLocator.Current.GetInstance<ImageDecoder>(),
            DiskCache = new DefaultDiskCache(),
            MemoryCache = new DefaultMemoryCache(),
        };

        Uri url;
        int maxSize;
        object token;

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

        public void Into<T>(Action<T> callback)
        {
            throw new System.NotImplementedException();
        }
    }
}