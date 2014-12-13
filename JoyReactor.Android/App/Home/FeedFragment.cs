using Android.OS;
using Android.Support.V4.Widget;
using Android.Support.V7.Widget;
using Android.Views;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;
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

		FeedViewModel viewModel;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
			viewModel = new FeedViewModel (ID.Factory.New (ID.IdConst.ReactorGood));
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			list.SetAdapter (adapter = new FeedAdapter (Activity));
			adapter.ChangeItemSource (viewModel.Posts);

			applyButton.SetCommand ("Click", viewModel.ApplyCommand);
			refresher.SetCommand ("Refresh", viewModel.RefreshCommand);

			viewModel
				.SetBinding (() => viewModel.HasNewItems, applyButton, () => applyButton.Visibility, BindingMode.OneWay)
				.ConvertSourceToTarget (s => s ? ViewStates.Visible : ViewStates.Gone);
			viewModel
				.SetBinding (() => viewModel.IsBusy, refresher, () => refresher.Refreshing, BindingMode.OneWay);
		}

		#region Collection change listener

		public override void OnStart ()
		{
			base.OnStart ();
			ChangeSubscriptionCommand.Register (this, viewModel.ChangeCurrentListIdCommand.Execute);
		}

		public override void OnStop ()
		{
			base.OnStop ();
			ChangeSubscriptionCommand.Unregister (this);
		}

		#endregion

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
	}
}