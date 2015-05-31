using Android.Support.V7.Widget;
using Android.Views;
using JoyReactor.Android.Widget;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Posts
{
    public class HeaderRow : RecyclerView.ViewHolder, PostFragment.Adapter.PostViewHolder
    {
        PostViewModel viewmodel;
        WebImageView image;
        FixedAspectPanel panel;

        public HeaderRow(ViewGroup parent, PostViewModel viewmodel)
            : base(LayoutInflater.FromContext(parent.Context).Inflate(Resource.Layout.layout_post, parent, false))
        {
            this.viewmodel = viewmodel;
            image = ItemView.FindViewById<WebImageView>(Resource.Id.image);
            panel = ItemView.FindViewById<FixedAspectPanel>(Resource.Id.imagePanel);
        }

        public void OnBindViewHolder(int position)
        {
            image.ImageSource = viewmodel.Image;
            panel.Aspect = viewmodel.ImageAspect;
        }
    }
}