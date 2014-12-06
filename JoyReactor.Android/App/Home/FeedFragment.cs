using Android.OS;
using Android.Support.V4.Widget;
using Android.Support.V7.Widget;
using Android.Views;
using JoyReactor.Core;
using JoyReactor.Core.Controllers;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		SwipeRefreshLayout refresher;
		RecyclerView list;
		FeedAdapter adapter;
		View applyButton;

		FeedController controller;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;

			controller = new FeedController (ID.Factory.New (ID.IdConst.ReactorGood));
			ChangeSubscriptionCommand.Register (this, controller.OnChangeCurrentListId);
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			list.SetAdapter (adapter = new FeedAdapter (Activity));
			adapter.ClickMore += (sender, e) => controller.OnButtonMoreClicked ();

			applyButton.Click += (sender, e) => controller.OnApplyButtonClicked ();
			refresher.Refresh += (sender, e) => controller.OnRefreshInvoked ();
			controller.InvalidateUiCallback = InvalidateUi;
			InvalidateUi ();
		}

		void InvalidateUi ()
		{
			if (IsViewInflated) {
				applyButton.Visibility = controller.HasNewItems ? ViewStates.Visible : ViewStates.Gone;
				adapter.ReplaceAll (controller.Posts);
				refresher.Refreshing = controller.SyncInProgress;
			}
		}

		bool IsViewInflated { get { return View != null; } }

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.fragment_feed, null);
			list = v.FindViewById<RecyclerView> (Resource.Id.List);
			list.SetLayoutManager (new StaggeredGridLayoutManager (2, StaggeredGridLayoutManager.Vertical));
			list.AddItemDecoration (new DividerItemDecoration (2.5f));
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