using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.Widget;
using Android.Util;
using Android.Views;
using Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Inject;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using System.Threading.Tasks;

namespace JoyReactor.Android.App.Post
{
	public class PostFragment : BaseFragment
	{
		private IPostModel model = ServiceLocator.Current.GetInstance<IPostModel> ();

		private ListView list;
		private List<Comment> comments;
		private List<CommentAttachment> attachments;
		private JoyReactor.Core.Model.DTO.Post post;

		private View header;

		public async override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			await InitializeHeader ();

			var adapter = new PostAdapter (this);
			list.Adapter = adapter;
			comments = await model.GetTopCommentsAsync (post.Id, int.MaxValue);
			adapter.NotifyDataSetChanged ();
		}

		public static PostFragment NewFragment (int position)
		{
			return NewFragment<PostFragment> (position);
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.fragment_post, null);
			list = v.FindViewById<ListView> (Resource.Id.list);
			list.AddHeaderView (header = View.Inflate (Activity, Resource.Layout.laytou_post_header, null));
			return v;
		}

		private async Task InitializeHeader() {
			post = await model.GetPostAsync (Arguments.GetInt (Arg1));
			attachments = await model.GetAttachmentsAsync (post.Id);

			var iv = header.FindViewById<WebImageView> (Resource.Id.image);
			var ap = header.FindViewById<FixedAspectPanel> (Resource.Id.aspectPanel);
			var ats = header.FindViewById<FixedGridView> (Resource.Id.attachments);

			iv.ImageSource = post == null ? null : post.Image;
			ap.Aspect = post == null ? 1 : (float)post.ImageWidth / post.ImageHeight;
			ats.Adapter = new AttachmentAdapter { fragment = this, items = attachments } ;
		}

		private class PostAdapter : BaseAdapter
		{
			private PostFragment fragment;

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

		private class AttachmentAdapter : BaseAdapter {

			internal List<CommentAttachment> items;
			internal IImageModel model = ServiceLocator.Current.GetInstance<IImageModel> ();
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