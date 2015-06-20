using Android.OS;
using Android.Views;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App
{
    public class UpdateNotificationFragment : BaseFragment
    {
        UpdateNotificationViewModel viewmodel;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;
            viewmodel = new UpdateNotificationViewModel();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.fragment_update_notification, container, false);

            view.FindViewById(Resource.Id.open).SetCommand("Click", viewmodel.OpenCommand);
            Bindings
                .Add(viewmodel, () => viewmodel.UpdateAvailable, view, () => view.Visibility)
                .ConvertSourceToTarget(s => s ? ViewStates.Visible : ViewStates.Gone);

            return view;
        }
    }
}