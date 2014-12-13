using System;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Drawing;
using Android.Content;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
	class FeedAdapter : RecyclerView.Adapter
	{
		const int TypeContent = Resource.Layout.ItemFeed;
		const int TypeDivider = Resource.Layout.item_comment;

		public ID ListId { get; set; }

		ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();

		int maxWidth;
		Context context;
		ObservableCollection<ViewModelBase> items;

		public FeedAdapter (Context context)
		{
			this.context = context;
			maxWidth = (int)(200 * context.Resources.DisplayMetrics.Density);
		}

		public void ChangeItemSource (ObservableCollection<ViewModelBase> value)
		{
			if (items != value) {
				if (items != null)
					items.CollectionChanged -= HandleCollectionChanged;

				items = value;
				items.CollectionChanged += HandleCollectionChanged;
				NotifyDataSetChanged ();
			}
		}

		void HandleCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			NotifyDataSetChanged ();
		}

		#region implemented abstract members of Adapter

		public override int GetItemViewType (int position)
		{
			return items [position] is FeedViewModel.ContentViewModel ? TypeContent : TypeDivider;
		}

		public override void OnBindViewHolder (RecyclerView.ViewHolder holder, int position)
		{
			if (holder.ItemViewType == TypeContent)
				GetViewForItem (holder.ItemView, position);
			else
				GetViewForFooter (holder.ItemView);
		}

		public override RecyclerView.ViewHolder OnCreateViewHolder (ViewGroup parent, int viewType)
		{
			return viewType == TypeDivider
				? new ViewHolder (GetViewForFooter (null))
				: new ViewHolder (View.Inflate (context, Resource.Layout.ItemFeed, null));
		}

		public override int ItemCount { 
			get { return items == null ? 0 : items.Count; }
		}

		#endregion

		View GetViewForFooter (View convertView)
		{
			if (convertView == null) {
				convertView = new Button (context);
				((Button)convertView).Text = "FOOTER";
//				convertView.SetClick (ClickMore);
				convertView.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams (
					ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent) {
					FullSpan = true,
				};
			}
			return convertView;
		}

		void GetViewForItem (View convertView, int position)
		{
			var item = (FeedViewModel.ContentViewModel)items [position];

			// TODO
			//			var v = convertView.FindViewById (Resource.Id.action);
			// v.SetClick ((sender, e) => context.StartActivity (PostActivity.NewIntent (item.Id)));

			var iv = convertView.FindViewById<FixedSizeImageView> (Resource.Id.image);
			iv.ImageSize = new Size (item.ImageWidth, item.ImageHeight);

			iv.SetImageDrawable (null);
			iModel.Load (iv, item.Image == null ? null : new Uri (item.Image), maxWidth, s => iv.SetImageDrawable (s == null ? null : new BitmapDrawable ((Bitmap)s)));

			var ui = convertView.FindViewById<ImageView> (Resource.Id.user_image);
			ui.SetImageDrawable (null);
			iModel.Load (ui, item.UserImage, 0, s => ui.SetImageBitmap ((Bitmap)s));
			convertView.FindViewById<TextView> (Resource.Id.user_name).Text = item.UserName;
			convertView.FindViewById<TextView> (Resource.Id.title).Text = item.Title;
		}

		class ViewHolder : RecyclerView.ViewHolder
		{
			public ViewHolder (View view) : base (view)
			{
			}
		}
	}
}