using System;
using Android.Graphics;
using Android.Support.V17.Leanback.Widget;
using Android.Views;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.AndroidTv
{
    class PostPresenter : Presenter
    {
        static readonly Random Rand = new Random();

        public override void OnUnbindViewHolder(ViewHolder viewHolder)
        {
            // Nothing to do
        }

        public override ViewHolder OnCreateViewHolder(ViewGroup parent)
        {
            return new ViewHolder(new ImageCardView(parent.Context) { Focusable = true });
        }

        public override void OnBindViewHolder(ViewHolder viewHolder, Java.Lang.Object item)
        {
            var tag = ((PostWrapper)item).Tag;
            var image = (ImageCardView)viewHolder.View;
            image.SetBackgroundColor(Color.HSVToColor(new []
                {
                    360 * (float)Rand.NextDouble(),
                    (float)Rand.NextDouble() / 2 + 0.5f,
                    (float)Rand.NextDouble() / 2 + 0.5f
                }));
            image.TitleText = tag.Title;
            image.SetMainImageDimensions(313, 176);
        }

        internal class PostWrapper : Java.Lang.Object
        {
            internal Tag Tag { get; set; }
        }
    }
}