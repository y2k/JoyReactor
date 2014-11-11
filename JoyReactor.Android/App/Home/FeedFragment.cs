using System;
using System.Collections.Generic;
using Android.OS;
using Android.Support.V4.Widget;
using Android.Views;
using Com.Android.EX.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		SwipeRefreshLayout refresher;
		StaggeredGridView list;
		FeedAdapter adapter;
		View applyButton;

		IPostCollectionModel model = ServiceLocator.Current.GetInstance<IPostCollectionModel> ();
		PostCollectionState data;
		bool syncInProgress;
		ID id;

		public override  void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;

			LoadFirstPage (ID.Factory.New (ID.IdConst.ReactorGood));
			ChangeSubscriptionCommand.Register (this, LoadFirstPage);
		}

		async void LoadFirstPage (ID newId)
		{
			id = newId;
			data = null;
			InvalidateUi ();

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
			refresher.Refresh+= OnRefreshInvoked;
			InvalidateUi ();
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

		async void OnRefreshInvoked (object sender, EventArgs e)
		{
			syncInProgress = true;
			InvalidateUi ();

			await model.Reset (id);
			await model.SyncFirstPage (id);
			data = await model.Get (id);
			syncInProgress = false;
			InvalidateUi ();
		}

		void InvalidateUi ()
		{
			if (IsViewInflated) {
				applyButton.Visibility = data?.NewItemsCount > 0 ? ViewStates.Visible : ViewStates.Gone;
				adapter.Clear();
				adapter.ReplaceAll (data?.Posts);
				refresher.Refreshing = syncInProgress;
			}
		}

		bool IsViewInflated { get { return list != null; } }

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.fragment_feed, null);
			list = v.FindViewById<StaggeredGridView> (Resource.Id.List);
			list.SetItemMargin ((int)(4 * Resources.DisplayMetrics.Density));
			refresher = v.FindViewById<SwipeRefreshLayout> (Resource.Id.refresher);
			applyButton = v.FindViewById (Resource.Id.apply);
			return v;
		}

		public override void OnDestroy ()
		{
			base.OnDestroy ();
			ChangeSubscriptionCommand.Unregister (this);
		}
	}
}