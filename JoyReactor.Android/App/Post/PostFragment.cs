using System.Collections.Generic;
using System.Threading.Tasks;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Post
{
	public class PostFragment : BaseFragment
	{
		IPostModel model = ServiceLocator.Current.GetInstance<IPostModel> ();

		ListView list;
		List<Comment> comments;
		List<CommentAttachment> attachments;
		JoyReactor.Core.Model.DTO.Post post;
		ColorSwipeRefreshLayout refresher;

		View header;
		PostAdapter adapter;

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter = new PostAdapter (this);

			refresher.Refresh += (s, e) => ReloadData ();
			refresher.Refreshing = true;
			ReloadData ();
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.fragment_post, null);
			list = v.FindViewById<ListView> (Resource.Id.list);
			list.AddHeaderView (header = View.Inflate (Activity, Resource.Layout.laytou_post_header, null));
			refresher = v.FindViewById<ColorSwipeRefreshLayout> (Resource.Id.refresher);
			return v;
		}

		async void ReloadData() {
			await InitializeHeader ();

			comments = await model.GetTopCommentsAsync (post.Id, int.MaxValue);
			adapter.NotifyDataSetChanged ();

			refresher.Refreshing = false;
		}

		async Task InitializeHeader() {
			post = await model.GetPostAsync (Arguments.GetInt (Arg1));
			attachments = await model.GetAttachmentsAsync (post.Id);

			var iv = header.FindViewById<WebImageView> (Resource.Id.image);
			var ap = header.FindViewById<FixedAspectPanel> (Resource.Id.aspectPanel);
			var ats = header.FindViewById<FixedGridView> (Resource.Id.attachments);
			var desc = header.FindViewById<TextView> (Resource.Id.description);

			iv.ImageSource = post == null ? null : post.Image;
			ap.Aspect = post == null ? 1 : (float)post.ImageWidth / post.ImageHeight;
			ats.Adapter = new AttachmentAdapter { fragment = this, items = attachments } ;
			desc.Text = post.Content;
			desc.Visibility = string.IsNullOrEmpty (post.Content) ? ViewStates.Gone : ViewStates.Visible;
		}

		public static PostFragment NewFragment (int position)
		{
			return NewFragment<PostFragment> (position);
		}

		class PostAdapter : BaseAdapter
		{
			PostFragment fragment;

			public PostAdapter (PostFragment fragment)
			{
				this.fragment = fragment;
			}

			#region implemented abstract members of BaseAdapter

			public override Java.Lang.Object GetItem (int position)
			{
				return null;
			}

			public override long GetItemId (int position)
			{
				return position;
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				var v = convertView ?? View.Inflate (parent.Context, Resource.Layout.item_comment, null);
				var c = fragment.comments [position];
				v.FindViewById<TextView> (Resource.Id.title).Text = c.UserName + " - " + (c.Text ?? "").HtmlToString ();
				v.FindViewById<WebImageView> (Resource.Id.icon).ImageSource = c.UserImage;
				return v;
			}

			public override int Count {
				get { return fragment.comments == null ? 0 : fragment.comments.Count; }
			}

			#endregion
		}

		class AttachmentAdapter : BaseAdapter {

			internal List<CommentAttachment> items;
			internal ImageModel model = ServiceLocator.Current.GetInstance<ImageModel> ();
			internal PostFragment fragment;

			#region implemented abstract members of BaseAdapter

			public override Java.Lang.Object GetItem (int position)
			{
				return null;
			}

			public override long GetItemId (int position)
			{
				return items [position].Id;
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				var iv = (WebImageView)(convertView ?? new WebImageView (parent.Context, null));
				iv.SetScaleType (ImageView.ScaleType.CenterCrop);
				iv.ImageSource = model.CreateThumbnailUrl (items [position].Url, 
					(int)(100 * parent.Resources.DisplayMetrics.Density));

				iv.Click += (sender, e) => {
					((BaseActivity)fragment.Activity).NavigateToGallery(fragment.post.Id);
				};

				return iv;
			}

			public override int Count {
				get { return items == null ? 0 : items.Count; }
			}

			#endregion
		}
	}
}