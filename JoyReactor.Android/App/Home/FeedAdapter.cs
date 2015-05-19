using System;
using System.Collections.ObjectModel;
using Android.Content;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight;
using Humanizer;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Home
{
    class FeedAdapter : RecyclerView.Adapter
    {
        public ID ListId { get; set; }

        readonly ObservableCollection<ViewModelBase> items;

        public FeedAdapter(ObservableCollection<ViewModelBase> items)
        {
            this.items = items;
            items.CollectionChanged += (sender, e) => NotifyDataSetChanged();
        }

        #region RecyclerView.Adapter methods

        public override int ItemCount
        { 
            get { return items.Count; }
        }

        public override int GetItemViewType(int position)
        {
            return BaseViewHolder.GetItemViewType(items[position]);
        }

        public override RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
        {
            return BaseViewHolder.NewViewHolder(parent.Context, viewType);
        }

        public override void OnBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ((BaseViewHolder)holder).OnBindViewHolder(items[position]);
        }

        #endregion

        abstract class BaseViewHolder : RecyclerView.ViewHolder
        {
            internal BaseViewHolder(View view)
                : base(view)
            {
            }

            internal abstract void OnBindViewHolder(object item);

            internal static int GetItemViewType(object item)
            {
                return item is FeedViewModel.ContentViewModel ? 0 : 1;
            }

            internal static BaseViewHolder NewViewHolder(Context context, int viewType)
            {
                return (viewType == 0 ? (BaseViewHolder)new ContentViewHolder(context) : new FooterViewHolder(context));
            }
        }

        class ContentViewHolder : BaseViewHolder
        {
            const float MinImageAspect = 1f / 2f;
            Context context;

            public ContentViewHolder(Context context)
                : base(View.Inflate(context, Resource.Layout.item_feed, null))
            {
                this.context = context;
            }

            internal override void OnBindViewHolder(object item)
            {
                var vm = (FeedViewModel.ContentViewModel)item;

                ItemView.FindViewById<FixedAspectPanel>(Resource.Id.imagePanel).Aspect =
                    Math.Max(MinImageAspect, (float)vm.ImageWidth / vm.ImageHeight);
                var iv = ItemView.FindViewById<WebImageView>(Resource.Id.image);
                iv.ImageSize = 200 * context.Resources.DisplayMetrics.Density;
                iv.ImageSource = vm.Image;

                ItemView.FindViewById<TextView>(Resource.Id.time).Text = vm.Created.Humanize();
                ItemView.FindViewById<WebImageView>(Resource.Id.userImage).ImageSource = "" + vm.UserImage;
                ItemView.FindViewById<TextView>(Resource.Id.userName).Text = vm.UserName;

                ItemView.FindViewById(Resource.Id.action).SetClick((sender, e) => vm.OpenPostCommand.Execute(null));
            }
        }

        class FooterViewHolder : BaseViewHolder
        {
            public FooterViewHolder(Context context)
                : base(View.Inflate(context, Resource.Layout.item_post_divider, null))
            {
                ItemView.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent)
                {
                    FullSpan = true,
                };
            }

            internal override void OnBindViewHolder(object item)
            {
                var vm = (FeedViewModel.DividerViewModel)item;
                ItemView
                    .FindViewById(Resource.Id.dividerButton)
                    .SetClick((sender, e) => vm.LoadMoreCommand.Execute(null));
            }
        }
    }
}