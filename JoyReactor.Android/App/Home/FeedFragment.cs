using Android.OS;
using Android.Support.V4.Widget;
using Android.Support.V7.Widget;
using Android.Views;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		FeedViewModel viewModel;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
			viewModel = new FeedViewModel ();
            viewModel.Initialize (ID.Factory.New (ID.IdConst.ReactorGood));
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var view = inflater.Inflate (Resource.Layout.fragment_feed, null);

            view.FindViewById<RecyclerView> (Resource.Id.list).SetAdapter (new FeedAdapter (viewModel.Posts));

			var refresher = view.FindViewById<SwipeRefreshLayout> (Resource.Id.refresher);
			refresher.SetCommand ("Refresh", viewModel.RefreshCommand);
            AddBinding(viewModel, () => viewModel.IsBusy, refresher, () => refresher.Refreshing);

			var applyButton = view.FindViewById<ReloadButton> (Resource.Id.apply);
			applyButton.Command = viewModel.ApplyCommand;
			viewModel
				.SetBinding (() => viewModel.HasNewItems, applyButton, () => applyButton.Visibility, BindingMode.OneWay)
				.ConvertSourceToTarget (s => s ? ViewStates.Visible : ViewStates.Gone);

            var error = view.FindViewById(Resource.Id.error);
            AddBinding(viewModel, () => viewModel.Error, error, () => error.Visibility)
                .ConvertSourceToTarget(s => s == FeedViewModel.ErrorType.NotError ? ViewStates.Gone : ViewStates.Visible);

			return view;
		}
	}
}