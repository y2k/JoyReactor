using System;
using Android.Graphics;
using Android.Support.V17.Leanback.Widget;
using Android.Views;
using JoyReactor.Core.ViewModels;
using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using Android.Graphics.Drawables;

namespace JoyReactor.AndroidTv
{
    class PostPresenter : Presenter
    {
        ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();

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
            var tag = ((PostWrapper)item).Post as FeedViewModel.ContentViewModel;
            if (tag != null)
            {
                var image = (ImageCardView)viewHolder.View;
                image.TitleText = tag.Title;
                image.ContentText = "User: " + tag.UserName;
                image.SetMainImageDimensions(313, 176);

                image.MainImage = null;
                iModel.Load(
                    viewHolder, 
                    tag.Image == null ? null : new Uri(tag.Image), 
                    300, 
                    bitmap => image.MainImage = bitmap == null ? null : new BitmapDrawable((Bitmap)bitmap));
            }
        }

        internal class PostWrapper : Java.Lang.Object
        {
            internal ViewModelBase Post { get; set; }
        }
    }
}