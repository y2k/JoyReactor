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
using Com.Android.EX.Widget;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		private SwipeRefreshLayout refresher;
		private StaggeredGridView list;
		private FeedAdapter adapter;
		private View applyButton;

		private IPostCollectionModel model = ServiceLocator.Current.GetInstance<IPostCollectionModel> ();
		private PostCollectionState data;
		private ID id;
		private bool syncInProgress;

		public override async void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;

			id = ID.Factory.New (ID.IdConst.ReactorGood);
			data = await model.Get (id);
			syncInProgress = true;
			InvalidateUi ();
			await model.SyncFirstPage (id);
			data = await model.Get (id);
			syncInProgress = false;
			InvalidateUi ();
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			list.Adapter = adapter = new FeedAdapter (Activity);
			applyButton.Click += OnApplyButtonClicked;
			adapter.ClickMore += OnButtonMoreClicked;
			InvalidateUi ();
		}

		void InvalidateUi ()
		{
			if (data != null) {
				applyButton.Visibility = data.NewItemsCount > 0 ? ViewStates.Visible : ViewStates.Gone;
				adapter.AddAll (data.Posts);
				refresher.Refreshing = syncInProgress;
			}
		}

		async void OnApplyButtonClicked (object sender, EventArgs e)
		{
			await model.ApplyNewItems (id);
			data = await model.Get (id);
			InvalidateUi ();
		}
		 
		async void OnButtonMoreClicked (object sender, EventArgs e)
		{
			syncInProgress = true;
			InvalidateUi ();
			await model.SyncNextPage (id);
			data = await model.Get (id);
			syncInProgress = false;
			InvalidateUi ();
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.fragment_feed, null);
			list = v.FindViewById<StaggeredGridView> (Resource.Id.List);
			list.SetItemMargin ((int)(4 * Resources.DisplayMetrics.Density));
			refresher = v.FindViewById<SwipeRefreshLayout> (Resource.Id.refresher);
			applyButton = v.FindViewById (Resource.Id.apply);
			return v;
		}
	}
}