using System.Collections.ObjectModel;
using System.Collections.Specialized;
using Android.Content;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Home
{
	class FeedAdapter : RecyclerView.Adapter
	{
		const int TypeContent = Resource.Layout.item_feed;
		const int TypeDivider = Resource.Layout.item_comment;

		public ID ListId { get; set; }

		int maxWidth;
		Context context;
		ObservableCollection<ViewModelBase> items;

		public FeedAdapter (Context context) : this (context, null)
		{
		}

		public FeedAdapter (Context context, ObservableCollection<ViewModelBase> value)
		{
			this.context = context;
			maxWidth = (int)(200 * context.Resources.DisplayMetrics.Density);
			ChangeItemSource (value);
		}

		public void ChangeItemSource (ObservableCollection<ViewModelBase> value)
		{
			if (items != value) {
				if (items != null)
					items.CollectionChanged -= HandleCollectionChanged;

				items = value;
				if (items != null) 
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
				BindContent (holder.ItemView, position);
			else
				BindFooter (holder.ItemView, (FeedViewModel.DividerViewModel)items [position]);
		}

		public override RecyclerView.ViewHolder OnCreateViewHolder (ViewGroup parent, int viewType)
		{
			return viewType == TypeDivider
				? new ViewHolder (CreateViewForFooter ())
				: new ViewHolder (View.Inflate (context, Resource.Layout.item_feed, null));
		}

		public override int ItemCount { 
			get { return items == null ? 0 : items.Count; }
		}

		#endregion

		void BindFooter (View footer, FeedViewModel.DividerViewModel viewModel)
		{
            footer
                .FindViewById(Resource.Id.dividerButton)
                .SetClick ((sender, e) => viewModel.LoadMoreCommand.Execute (null));
		}

		View CreateViewForFooter ()
		{
			var convertView = View.Inflate (context, Resource.Layout.item_post_divider, null);
			convertView.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams (
				ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent) {
				FullSpan = true,
			};
			return convertView;
		}

        void BindContent (View view, int position)
		{
			var item = (FeedViewModel.ContentViewModel)items [position];

            view.FindViewById<FixedAspectPanel>(Resource.Id.imagePanel).Aspect = (float)item.ImageWidth / item.ImageHeight;
            var iv = view.FindViewById<WebImageView>(Resource.Id.image);
            iv.ImageSize = maxWidth;
            iv.ImageSource = item.Image;

            view.FindViewById<WebImageView> (Resource.Id.userImage).ImageSource = "" + item.UserImage;
            view.FindViewById<TextView> (Resource.Id.userName).Text = item.UserName;

            view.FindViewById (Resource.Id.action).SetClick ((sender, e) => item.OpenPostCommand.Execute (null));
		}

		class ViewHolder : RecyclerView.ViewHolder
		{
			public ViewHolder (View view)
				: base (view)
			{
			}
		}
	}
}