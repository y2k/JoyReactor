using System;
using System.Collections.ObjectModel;
using Android.Content;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using Humanizer;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.ViewModels;
using Android.Graphics;

namespace JoyReactor.Android.App.Home
{
    class FeedAdapter : RecyclerView.Adapter
    {
        public ID ListId { get; set; }

        readonly ObservableCollection<PostItemViewModel> items;
        readonly FeedViewModel viewmodel;

        public FeedAdapter(ObservableCollection<PostItemViewModel> items, FeedViewModel viewmodel)
        {
            this.viewmodel = viewmodel;
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
            return BaseViewHolder.NewViewHolder(parent.Context, viewType, viewmodel);
        }

        public override void OnBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            ((BaseViewHolder)holder).OnBindViewHolder(items[position], position);
        }

        #endregion

        abstract class BaseViewHolder : RecyclerView.ViewHolder
        {
            internal BaseViewHolder(View view)
                : base(view)
            {
            }

            internal abstract void OnBindViewHolder(object item, int position);

            internal static int GetItemViewType(object item)
            {
                return item is PostItemViewModel.Divider ? 1 : 0;
            }

            internal static BaseViewHolder NewViewHolder(Context context, int viewType, FeedViewModel viewmodel)
            {
                return viewType == 0 
                    ? (BaseViewHolder)new ContentViewHolder(context, viewmodel) 
                    : new FooterViewHolder(context, viewmodel);
            }
        }

        class ContentViewHolder : BaseViewHolder
        {
            const float MinImageAspect = 1f / 2f;
            readonly Context context;
            readonly FeedViewModel viewmodel;

            public ContentViewHolder(Context context, FeedViewModel viewmodel)
                : base(View.Inflate(context, Resource.Layout.item_feed, null))
            {
                this.viewmodel = viewmodel;
                this.context = context;
            }

            internal override void OnBindViewHolder(object item, int position)
            {
                var vm = (PostItemViewModel)item;

                ItemView.FindViewById<FixedAspectPanel>(Resource.Id.imagePanel).Aspect =
                    Math.Max(MinImageAspect, vm.ImageAspect);
                var iv = ItemView.FindViewById<WebImageView>(Resource.Id.image);
                iv.ImageSize = 200 * context.Resources.DisplayMetrics.Density;
                iv.ImageSource = vm.Image;

                ItemView.FindViewById<TextView>(Resource.Id.time).Text = 
                    vm.Created.ToUniversalTime().Humanize();
                ItemView.FindViewById<WebImageView>(Resource.Id.userImage).ImageSource = "" + vm.UserImage;
                ItemView.FindViewById<TextView>(Resource.Id.userName).Text = vm.UserName;

                ItemView.FindViewById(Resource.Id.videoMark).Visibility = 
                    vm.IsVideo ? ViewStates.Visible : ViewStates.Gone;

                var button = ItemView.FindViewById<CommandButton>(Resource.Id.action);
                button.ClickCommandArgument = button.LongClickCommandArgument = position;
                button.ClickCommand = viewmodel.SelectItemCommand;
                button.LongClickCommand = vm.OpenImageCommand;
            }
        }

        class FooterViewHolder : BaseViewHolder
        {
            readonly FeedViewModel viewmodel;

            public FooterViewHolder(Context context, FeedViewModel viewmodel)
                : base(View.Inflate(context, Resource.Layout.item_post_divider, null))
            {
                this.viewmodel = viewmodel;
                ItemView.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent)
                {
                    FullSpan = true,
                };
            }

            internal override void OnBindViewHolder(object item, int position)
            {
                ItemView
                    .FindViewById(Resource.Id.dividerButton)
                    .SetClick((sender, e) => viewmodel.SelectItemCommand.Execute(position));
            }
        }
    }
}