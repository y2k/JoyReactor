using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model.DTO;
using Com.Android.EX.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using Android.Graphics;
using JoyReactor.Android.Widget;
using System.Drawing;
using JoyReactor.Android.App.Post;
using JoyReactor.Core;
using Microsoft.Practices.ServiceLocation;
using Android.Graphics.Drawables;
using JoyReactor.Android.App.Base;

namespace JoyReactor.Android.App.Home
{
	class FeedAdapter : ArrayAdapter<JoyReactor.Core.Model.DTO.Post> 
	{
		private IImageModel iModel = ServiceLocator.Current.GetInstance<IImageModel>();
		private int maxWidth;

		public ID ListId { get; set; }
		public event EventHandler ClickMore;

		public FeedAdapter(Context context) : base(context, 0) 
		{
			maxWidth = (int) (200 * context.Resources.DisplayMetrics.Density);
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

		public override int GetItemViewType (int position)
		{
			return position == Count - 1 ? 1 : 0;
		}

		public override View GetView (int position, View convertView, ViewGroup parent)
		{
			return position == Count - 1
				? GetViewForFooter(convertView, position, parent)
				: GetViewForItem (convertView, position, parent);
		}

		private View GetViewForFooter(View convertView, int position, ViewGroup parent)
		{
			if (convertView == null) {
				convertView = new Button (parent.Context);
				((Button)convertView).Text = "FOOTER";
				convertView.SetClick ((s, e) => ClickMore (s, e));
				convertView.LayoutParameters = new StaggeredGridView.LayoutParams (StaggeredGridView.LayoutParams.WrapContent) { Span = 99 };
			}
			return convertView;
		}

		private View GetViewForItem (View convertView, int position, ViewGroup parent)
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
			iModel.Load (iv, item.Image == null ? null : new Uri (item.Image), maxWidth, s => iv.SetImageDrawable (s == null || s.Image == null ? null : new BitmapDrawable ((Bitmap)s.Image)));
			var ui = convertView.FindViewById<ImageView> (Resource.Id.user_image);
			ui.SetImageDrawable (null);
			iModel.Load (ui, item.UserImage == null ? null : new Uri (item.UserImage), 0, s => ui.SetImageBitmap ((Bitmap)s.Image));
			convertView.FindViewById<TextView> (Resource.Id.user_name).Text = item.UserName;
			convertView.FindViewById<TextView> (Resource.Id.title).Text = item.Title;
			return convertView;
		}
	}
}