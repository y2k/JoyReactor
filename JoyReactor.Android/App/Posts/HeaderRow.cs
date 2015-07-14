using System.Linq;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Posts
{
    public class HeaderRow : RecyclerView.ViewHolder, PostFragment.Adapter.PostViewHolder
    {
        PostViewModel viewmodel;
        WebImageView image;
        ViewGroup thumbnails;
        TextView imageCount;

        public HeaderRow(ViewGroup parent, PostViewModel viewmodel)
            : base(LayoutInflater.FromContext(parent.Context).Inflate(Resource.Layout.layout_post, parent, false))
        {
            this.viewmodel = viewmodel;
            image = ItemView.FindViewById<WebImageView>(Resource.Id.image);
            image.SetCommand(viewmodel.OpenImageCommand);
            thumbnails = ItemView.FindViewById<ViewGroup>(Resource.Id.thumbnails);
            imageCount = ItemView.FindViewById<TextView>(Resource.Id.imageCount);
        }

        public void OnBindViewHolder(int position)
        {
            image.ImageSizeDip = 300;
            image.ImageSource = viewmodel.Image;

            for (int i = 0; i < thumbnails.ChildCount; i++)
            {
                var iv = (WebImageView)thumbnails.GetChildAt(i);
                iv.ImageSource = viewmodel.CommentImages.Skip(i).FirstOrDefault();
                iv.SetCommand(viewmodel.OpenThumbnailCommand, i);
            }

            #if FUTURE
            var notVisibleImageCount = viewmodel.CommentImages.Count - thumbnails.ChildCount;
            #else
            var notVisibleImageCount = 0;
            #endif
            imageCount.Visibility = notVisibleImageCount > 0 ? ViewStates.Visible : ViewStates.Gone;
            imageCount.Text = "+" + notVisibleImageCount;
        }
    }
}