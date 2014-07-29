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

namespace JoyReactor.Android.App.Post
{
	public class PostFragment : BaseFragment
	{
		private IPostModel model = ServiceLocator.Current.GetInstance<IPostModel> ();

		private ListView list;
		private List<Comment> comments;
		private List<CommentAttachment> attachments;
		private JoyReactor.Core.Model.DTO.Post post;

		public async override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			var adapter = new PostAdapter (this);
			list.Adapter = adapter;

			post = await model.GetPostAsync (Arguments.GetInt (Arg1));
			adapter.NotifyDataSetChanged ();
			comments = await model.GetTopCommentsAsync (post.Id, int.MaxValue);
			adapter.NotifyDataSetChanged ();
			attachments = await model.GetAttachmentsAsync (post.Id);
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
			return v;
		}

		private class PostAdapter : BaseAdapter
		{
			private PostFragment fragment;

			public PostAdapter (PostFragment fragment)
			{
				this.fragment = fragment;
			}

			#region implemented abstract members of BaseAdapter

			public override int GetItemViewType (int position)
			{
				return position < 2 ? position : 2;
			}

			public override int ViewTypeCount {
				get { return 3; }
			}

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
				switch (GetItemViewType (position)) {
				case 0:
					convertView = convertView ?? View.Inflate (parent.Context, Resource.Layout.laytou_post_header, null);
					var p = fragment.post;

//					var z = ((ViewGroup)((FrameLayout)convertView).GetChildAt (0)).GetChildAt (1);

					var iv = convertView.FindViewById<WebImageView> (Resource.Id.image);
					var ap = convertView.FindViewById<FixedAspectPanel> (Resource.Id.aspectPanel);
					var ats = convertView.FindViewById<PostImagesPreviewControl> (Resource.Id.attachments);

					iv.ImageSource = p == null ? null : p.Image;
					ap.Aspect = p == null ? 1 : (float)p.ImageWidth / p.ImageHeight;
					ats.Adapter = new AttachmentAdapter { items = fragment.attachments } ;

					return convertView;
				case 1:
					return convertView ?? new View (parent.Context);
				default:
					var v3 = convertView ?? View.Inflate (parent.Context, Resource.Layout.item_comment, null);
					var c = fragment.comments [position - 2];
					v3.FindViewById<TextView> (Resource.Id.title).Text = c.UserName + " - " + (c.Text ?? "").HtmlToString ();
					v3.FindViewById<WebImageView> (Resource.Id.icon).ImageSource = c.UserImage;
					return v3;
				}
			}

			public override int Count {
				get { return 2 + (fragment.comments == null ? 0 : fragment.comments.Count); }
			}

			#endregion

			private class AttachmentAdapter : BaseAdapter {

				internal List<CommentAttachment> items;

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
					iv.SetMinimumWidth (100);
					iv.SetMinimumHeight (100);
					iv.ImageSource = items [position].Url;
					return iv;
				}

				public override int Count {
					get { return items == null ? 0 : items.Count; }
				}

				#endregion
			}

			//
		}
	}
}