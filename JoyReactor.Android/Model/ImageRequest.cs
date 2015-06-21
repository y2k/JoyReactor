using System;
using System.Threading.Tasks;
using Android.Graphics;
using Android.Widget;
using JoyReactor.Core.Model.Images;

namespace JoyReactor.Android.Model
{
    public class ImageRequest : BaseImageRequest
    {
        protected override async Task<object> DecodeImageAsync(byte[] data)
        {
            return await BitmapFactory.DecodeByteArrayAsync(data, 0, data.Length);
        }

        protected override void SetToTarget(object target, object image)
        {
            var iv = target as ImageView;
            if (iv != null)
            {
                if (image == null)
                    iv.SetImageDrawable(null);
                else
                    iv.SetImageBitmap((Bitmap)image);
                return;
            }
            throw new ArgumentException("target = " + target);
        }

        protected override BaseMemoryCache CreateMemoryCache()
        {
            return new MemoryCache();
        }

        class MemoryCache : BaseMemoryCache
        {
            protected override int GetSize(object image)
            {
                return image == null ? 0 : ((Bitmap)image).ByteCount;
            }

            protected override int GetMaxSize()
            {
                return 4 * 1024 * 1024;
            }
        }
    }
}