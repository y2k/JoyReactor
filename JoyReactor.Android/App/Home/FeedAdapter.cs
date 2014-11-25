using System;
using System.Collections.Generic;
using System.Drawing;
using Android.Content;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Post;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
	class FeedAdapter : RecyclerView.Adapter
	{
		public ID ListId { get; set; }

		public event EventHandler ClickMore;

		ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();
		int maxWidth;
		Context context;
		List<JoyReactor.Core.Model.DTO.Post> items = new List<JoyReactor.Core.Model.DTO.Post> ();

		public FeedAdapter (Context context)
		{
			this.context = context;
			maxWidth = (int)(200 * context.Resources.DisplayMetrics.Density);
		}

		//		public FeedAdapter (Context context) : base (context, 0)
		//		{
		//			maxWidth = (int)(200 * context.Resources.DisplayMetrics.Density);
		//		}
		//
		//		public override int Count {
		//			get {
		//				return base.Count + 1;
		//			}
		//		}
		//
		//		public override int ViewTypeCount {
		//			get {
		//				return 2;
		//			}
		//		}
		
		public void ReplaceAll (IEnumerable<JoyReactor.Core.Model.DTO.Post> newItems)
		{
			items.Clear ();
			if (newItems != null)
				items.AddRange (newItems);
			NotifyDataSetChanged ();
		}
		
		//		public override int GetItemViewType (int position)
		//		{
		//			return position == Count - 1 ? 1 : 0;
		//		}
		//
		//		public override View GetView (int position, View convertView, ViewGroup parent)
		//		{
		//			return position == Count - 1
		//				? GetViewForFooter (convertView, parent)
		//				: GetViewForItem (convertView, position, parent);
		//		}
		//
		//		View GetViewForFooter (View convertView, ViewGroup parent)
		//		{
		//			if (convertView == null) {
		//				convertView = new Button (parent.Context);
		//				((Button)convertView).Text = "FOOTER";
		//				convertView.SetClick ((s, e) => ClickMore (s, e));
		////				convertView.LayoutParameters = new StaggeredGridView.LayoutParams (StaggeredGridView.LayoutParams.WrapContent) { Span = 99 };
		//			}
		//			return convertView;
		//		}
		
		void GetViewForItem (View convertView, int position)
		{
			var item = items [position];
			var v = convertView.FindViewById (Resource.Id.action);
			v.SetClick ((sender, e) => context.StartActivity (PostActivity.NewIntent (item.Id)));
			var iv = convertView.FindViewById<FixedSizeImageView> (Resource.Id.image);
			iv.ImageSize = new Size (item.ImageWidth, item.ImageHeight);
			iv.SetImageDrawable (null);
			iModel.Load (iv, item.Image == null ? null : new Uri (item.Image), maxWidth, s => iv.SetImageDrawable (s == null ? null : new BitmapDrawable ((Bitmap)s)));
			var ui = convertView.FindViewById<ImageView> (Resource.Id.user_image);
			ui.SetImageDrawable (null);
			iModel.Load (ui, item.UserImage == null ? null : new Uri (item.UserImage), 0, s => ui.SetImageBitmap ((Bitmap)s));
			convertView.FindViewById<TextView> (Resource.Id.user_name).Text = item.UserName;
			convertView.FindViewById<TextView> (Resource.Id.title).Text = item.Title;
		}

		#region implemented abstract members of Adapter

		public override void OnBindViewHolder (RecyclerView.ViewHolder holder, int position)
		{
			GetViewForItem (holder.ItemView, position);
		}

		public override RecyclerView.ViewHolder OnCreateViewHolder (ViewGroup parent, int position)
		{
			return new ViewHolder (View.Inflate (context, Resource.Layout.ItemFeed, null));
		}

		public override int ItemCount {
			get {
				return items.Count;
			}
		}

		#endregion

		class ViewHolder : RecyclerView.ViewHolder
		{
			public ViewHolder (View view) : base (view)
			{
			}
		}
	}
}