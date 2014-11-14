using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using Android.Content;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.Views;
using Android.Widget;
using Com.Android.EX.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Post;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
	class FeedAdapter : ArrayAdapter<JoyReactor.Core.Model.DTO.Post>
	{
		ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();
		int maxWidth;

		public ID ListId { get; set; }

		public event EventHandler ClickMore;

		public FeedAdapter (Context context) : base (context, 0)
		{
			maxWidth = (int)(200 * context.Resources.DisplayMetrics.Density);
		}

		public override int Count {
			get {
				return base.Count + 1;
			}
		}

		public override int ViewTypeCount {
			get {
				return 2;
			}
		}

		public void ReplaceAll (IEnumerable<JoyReactor.Core.Model.DTO.Post> items)
		{
			Clear ();
			if (items != null)
				AddAll (items.ToList ());
		}

		public override int GetItemViewType (int position)
		{
			return position == Count - 1 ? 1 : 0;
		}

		public override View GetView (int position, View convertView, ViewGroup parent)
		{
			return position == Count - 1
				? GetViewForFooter (convertView, parent)
				: GetViewForItem (convertView, position, parent);
		}

		View GetViewForFooter (View convertView, ViewGroup parent)
		{
			if (convertView == null) {
				convertView = new Button (parent.Context);
				((Button)convertView).Text = "FOOTER";
				convertView.SetClick ((s, e) => ClickMore (s, e));
				convertView.LayoutParameters = new StaggeredGridView.LayoutParams (StaggeredGridView.LayoutParams.WrapContent) { Span = 99 };
			}
			return convertView;
		}

		View GetViewForItem (View convertView, int position, ViewGroup parent)
		{
			if (convertView == null) {
				convertView = View.Inflate (parent.Context, Resource.Layout.ItemFeed, null);
				convertView.LayoutParameters = new StaggeredGridView.LayoutParams (StaggeredGridView.LayoutParams.WrapContent);
			}
			var item = GetItem (position);
			var v = convertView.FindViewById (Resource.Id.action);
			v.SetClick ((sender, e) => Context.StartActivity (PostActivity.NewIntent (item.Id)));
			var iv = convertView.FindViewById<FixedSizeImageView> (Resource.Id.image);
			iv.ImageSize = new Size (item.ImageWidth, item.ImageHeight);
			iv.SetImageDrawable (null);
			iModel.Load (iv, item.Image == null ? null : new Uri (item.Image), maxWidth, s => iv.SetImageDrawable (s == null  ? null : new BitmapDrawable ((Bitmap)s)));
			var ui = convertView.FindViewById<ImageView> (Resource.Id.user_image);
			ui.SetImageDrawable (null);
			iModel.Load (ui, item.UserImage == null ? null : new Uri (item.UserImage), 0, s => ui.SetImageBitmap ((Bitmap)s));
			convertView.FindViewById<TextView> (Resource.Id.user_name).Text = item.UserName;
			convertView.FindViewById<TextView> (Resource.Id.title).Text = item.Title;
			return convertView;
		}
	}
}