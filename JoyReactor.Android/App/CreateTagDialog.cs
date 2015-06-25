using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.ViewModels;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;

namespace JoyReactor.Android.App
{
    public class CreateTagDialog : BaseDialogFragment
    {
        CreateTagViewModel viewModel;
        EditText name;
        ViewAnimator animator;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;
            viewModel = new CreateTagViewModel();
            MessengerInstance.Register<CreateTagViewModel.CloseMessage>(
                this, _ => DismissAllowingStateLoss());
        }

        public override Dialog OnCreateDialog(Bundle savedInstanceState)
        {
            var dialog = base.OnCreateDialog(savedInstanceState);
            dialog.SetTitle(Resource.String.create_tag);
            return dialog;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.dialog_create_tag, null);
            name = view.FindViewById<EditText>(Resource.Id.name);
            animator = view.FindViewById<ViewAnimator>(Resource.Id.animator);
            return view;
        }

        public override void OnActivityCreated(Bundle savedInstanceState)
        {
            base.OnActivityCreated(savedInstanceState);

            Bindings.Add(viewModel, () => viewModel.Name, name, () => name.Text, BindingMode.TwoWay);
            Bindings
                .Add(viewModel, () => viewModel.NameError)
                .WhenSourceChanges(() => name.Error = viewModel.NameError ? GetString(Resource.String.required_field) : null);
            Bindings
                .Add(viewModel, () => viewModel.IsBusy)
                .WhenSourceChanges(() => animator.DisplayedChild = viewModel.IsBusy ? 1 : 0);

            View.FindViewById(Resource.Id.cancel).Click += (sender, e) => Dismiss();
            View.FindViewById(Resource.Id.ok).SetCommand(viewModel.CreateCommand);
        }
    }
}