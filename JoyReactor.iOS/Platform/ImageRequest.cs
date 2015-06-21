using System;
using JoyReactor.Core.Model.Images;
using System.Threading.Tasks;
using UIKit;
using Foundation;

namespace JoyReactor.iOS.Platform
{
    public class ImageRequest : BaseImageRequest
    {
        protected override Task<object> DecodeImageAsync(byte[] data)
        {
            return Task.Run<object>(() => new UIImage(NSData.FromArray(data)));
        }

        protected override void SetToTarget(object target, object image)
        {
            var iv = target as UIImageView;
            if (iv == null)
                throw new ArgumentException("target = " + target);
            iv.Image = (UIImage)image;
        }

        protected override BaseMemoryCache CreateMemoryCache()
        {
            return new MemoryCache();
        }

        class MemoryCache : BaseMemoryCache
        {
            protected override int GetSize(object image)
            {
                var uiImage = (UIImage)image;
                return uiImage == null ? 0 : (int)(uiImage.Size.Width * uiImage.Size.Height * 4);
            }

            protected override int GetMaxSize()
            {
                return 4 * 1024 * 1024;
            }
        }
    }
}