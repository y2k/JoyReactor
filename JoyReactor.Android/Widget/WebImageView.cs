using System;
using Android.Content;
using Android.Util;
using Android.Widget;
using JoyReactor.Android.Model;

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

        public WebImageView(Context context, IAttributeSet attrs)
            : base(context, attrs)
        { 
            ImageSize = ImageSizeAuto;
        }

        void UpdateImageSource(string imageSource)
        {
            if (this.imageSource != imageSource)
            {
                this.imageSource = imageSource;
                new ImageRequest()
                    .SetUri(imageSource)
                    .CropIn(GetImageSize())
                    .To(this);
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