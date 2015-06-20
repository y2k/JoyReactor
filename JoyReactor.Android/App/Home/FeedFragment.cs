using Android.OS;
using Android.Support.V4.Widget;
using Android.Views;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Home
{
    public class FeedFragment : BaseFragment
    {
        FeedViewModel2 viewmodel;
        FeedRecyclerView list;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;
            viewmodel = new FeedViewModel2();

            MessengerInstance.Register<TagsViewModel.SelectTagMessage>(this, _ => list.ResetScrollToTop());
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.fragment_feed, null);

            list = view.FindViewById<FeedRecyclerView>(Resource.Id.list);
            list.SetAdapter(new FeedAdapter(viewmodel.Posts, viewmodel));

            var refresher = view.FindViewById<SwipeRefreshLayout>(Resource.Id.refresher);
//			refresher.SetCommand ("Refresh", viewModel.RefreshCommand);
            AddBinding(viewmodel, () => viewmodel.IsBusy, refresher, () => refresher.Refreshing);

            var applyButton = view.FindViewById<ReloadButton>(Resource.Id.apply);
            applyButton.Command = viewmodel.ApplyCommand;
            viewmodel
                .SetBinding(() => viewmodel.HasNewItems, applyButton, () => applyButton.Visibility, BindingMode.OneWay)
				.ConvertSourceToTarget(s => s ? ViewStates.Visible : ViewStates.Gone);

            var error = view.FindViewById(Resource.Id.error);
            AddBinding(viewmodel, () => viewmodel.Error, error, () => error.Visibility)
                .ConvertSourceToTarget(s => s == FeedViewModel.ErrorType.NotError ? ViewStates.Gone : ViewStates.Visible);

            return view;
        }
    }
}