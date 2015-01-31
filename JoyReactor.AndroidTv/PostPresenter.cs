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
        const int RowHeight = 300;
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
            var image = (ImageCardView)viewHolder.View;
            if (tag == null)
            {
                image.TitleText = null;
                image.ContentText = null;
                image.SetMainImageDimensions(RowHeight, RowHeight);

                LoadImage(null, image);
            }
            else
            {
                image.TitleText = tag.Title;
                image.ContentText = tag.UserName;
                image.SetMainImageDimensions(ComputeItemWidth(tag), RowHeight);

                LoadImage(tag.Image, image);
            }
        }

        void LoadImage(string imageUrl, ImageCardView image)
        {
            image.MainImage = null;
            var loadUrl = imageUrl ?? "http://d1luk0418egahw.cloudfront.net/static/images/guide/NoImage_592x444.jpg";
            iModel.Load(image, new Uri(loadUrl), RowHeight, bitmap => image.MainImage = bitmap == null ? null : new BitmapDrawable((Bitmap)bitmap));
        }

        static int ComputeItemWidth(FeedViewModel.ContentViewModel tag)
        {
            return tag.ImageHeight == 0 ? RowHeight : (int)(tag.ImageWidth / ((float)tag.ImageHeight / RowHeight));
        }

        internal class PostWrapper : Java.Lang.Object
        {
            internal ViewModelBase Post { get; set; }
        }
    }
}