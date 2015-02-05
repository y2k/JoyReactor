using System;
using Android.Graphics;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.AndroidTv.Helpers
{
    public class ColorSource : BackgroundChanger.IColorSource
    {
        Color color = Color.Gray;

        public ColorSource(object value)
        {
            var wrapper = value as PostPresenter.PostWrapper;
            if (wrapper != null)
            {
                var content = wrapper.Post as FeedViewModel.ContentViewModel;
                if (content != null)
                {
                    var imageUrl = content.Image;
                    if (imageUrl != null)
                    {
                        color = new Color(imageUrl.GetHashCode());
                        color.A = 0xFF;
                    }
                }
            }
        }

        public Color GetColor()
        {
            return color;
        }
    }
}