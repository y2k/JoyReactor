using Android.Content;
using Android.Graphics;
using Android.Util;
using Android.Widget;
using JoyReactor.Core.Model;
using System;

namespace JoyReactor.Android.Widget
{
    public class WebImageView : ImageView
    {
        public const float ImageSizeAuto = -1;

        string imageSource;

        public float ImageSize { get ; set; }

        public float ImageSizeDip
        { 
            get { return ImageSize / Resources.DisplayMetrics.Density; }
            set { ImageSize = value * Resources.DisplayMetrics.Density; }
        }

        public string ImageSource
        {
            get { return imageSource; }
            set { UpdateImageSource(value); }
        }

        public WebImageView(Context context, IAttributeSet attrs) : base(context, attrs) 
        { 
            ImageSize = ImageSizeAuto;
        }

        void UpdateImageSource(string imageSource)
        {
            if (this.imageSource != imageSource)
            {
                this.imageSource = imageSource;
                new ImageRequest()
                    .SetToken(this)
                    .SetUrl(imageSource)
                    .CropIn(GetImageSize())
                    .Into<Bitmap>(SetImageBitmap);
            }
        }

        int GetImageSize()
        {
            return ImageSize >= 0
                ? (int)ImageSize
                : Math.Max(LayoutParameters.Width, LayoutParameters.Height);
        }
    }
}