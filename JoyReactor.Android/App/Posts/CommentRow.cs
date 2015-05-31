using System.Linq;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Posts
{
    public class CommentRow : RecyclerView.ViewHolder, PostFragment.Adapter.PostViewHolder
    {
        PostViewModel viewmodel;

        TextView rating;
        TextView replies;
        View divider;
        TextView button;
        WebImageView avatar;
        WebImageView attach;

        public CommentRow(ViewGroup parent, PostViewModel viewmodel)
            : base(LayoutInflater.FromContext(parent.Context).Inflate(Resource.Layout.item_comment, parent, false))
        {
            this.viewmodel = viewmodel;
            rating = ItemView.FindViewById<TextView>(Resource.Id.rating);
            replies = ItemView.FindViewById<TextView>(Resource.Id.replies);
            divider = ItemView.FindViewById(Resource.Id.divider);
            button = ItemView.FindViewById<TextView>(Resource.Id.title);
            avatar = ItemView.FindViewById<WebImageView>(Resource.Id.icon);
            attach = ItemView.FindViewById<WebImageView>(Resource.Id.attachment);
        }

        public void OnBindViewHolder(int position)
        {
            var item = viewmodel.Comments[position];
            button.Text = item.Text;
            button.Visibility = string.IsNullOrEmpty(item.Text) ? ViewStates.Gone : ViewStates.Visible;

            attach.ImageSizeDip = 80;
            attach.ImageSource = item.Attachments.FirstOrDefault();
            attach.Visibility = item.Attachments.Count > 0 ? ViewStates.Visible : ViewStates.Gone;

            avatar.ImageSource = item.UserImage;
            rating.Text = "" + item.Rating;
            replies.Text = "" + item.ChildCount;
            divider.Visibility = item.IsReply ? ViewStates.Visible : ViewStates.Gone;

            ItemView.SetClick((sender, e) => item.NavigateCommand.Execute(null));
        }
    }
}