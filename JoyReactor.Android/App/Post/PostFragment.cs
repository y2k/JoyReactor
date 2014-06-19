using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using Android.Support.V4.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Android.App.Post
{
	public class PostFragment : BaseFragment
	{
		private IPostModel model = InjectService.Locator.GetInstance<IPostModel> ();

		private ListView list;
		private List<Comment> comments;
		private JoyReactor.Core.Model.DTO.Post post;

		public async override void OnActivityCreated (Bundle savedInstanceState) {
			base.OnActivityCreated (savedInstanceState);

			var adapter = new PostAdapter (this);
			list.Adapter = adapter;

			post = await model.GetPostAsync (((PostActivity)Activity).ListId, Arguments.GetInt (Arg1));
			adapter.NotifyDataSetChanged ();
			comments = await model.GetTopCommentsAsync (post.Id, 5);
			adapter.NotifyDataSetChanged ();
		}

		public static PostFragment NewFragment(int position) {
			return NewFragment<PostFragment> (position);
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			var v = inflater.Inflate (Resource.Layout.fragment_post, null);
			list = v.FindViewById<ListView> (Resource.Id.list);
			return v;
		}

		class PostAdapter : BaseAdapter {

			private PostFragment fragment;

			public PostAdapter(PostFragment fragment) {
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
					var v = (WebImageView)(convertView ?? new WebImageView (parent.Context, null));
					v.SetScaleType (ImageView.ScaleType.FitCenter);
					v.LayoutParameters = new ListView.LayoutParams (parent.Width, parent.Width);
					v.ImageSource = fragment.post == null ? null : fragment.post.Image;
					return v;
				case 1:
					return convertView ?? new View (parent.Context);
				default:
					var v3 = convertView ?? View.Inflate (parent.Context, Resource.Layout.item_comment, null);
					var c = fragment.comments [position - 2];
					v3.FindViewById<TextView> (Resource.Id.title).Text = c.Text.HtmlToString();
					v3.FindViewById<WebImageView> (Resource.Id.icon).ImageSource = c.UserImage;
					return v3;
				}
			}

			public override int Count {
				get { return 2 + (fragment.comments == null ? 0 : fragment.comments.Count); }
			}

			#endregion
		}
	}
}