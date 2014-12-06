using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Controllers;
using JoyReactor.Android.App.Base;

namespace JoyReactor.Android.App
{
	public class CreateTagDialog : BaseDialogFragment
	{
		CreateTagController controller;

		EditText name;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
			controller = new CreateTagController ();
		}

		public override Dialog OnCreateDialog (Bundle savedInstanceState)
		{
			var dialog = base.OnCreateDialog (savedInstanceState);
			dialog.SetTitle (Resource.String.create_tag);
			return dialog;
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var view = inflater.Inflate (Resource.Layout.dialog_create_tag, null);
			name = (EditText)view.FindViewById (Resource.Id.name);
			return view;
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);
			controller.InvalidateUiCallback = HandleInvalidateUi;

			name.TextChanged += (sender, e) => controller.Name = name.Text;
			View.FindViewById (Resource.Id.ok).Click += (sender, e) => controller.CreateCommand.Execute (null);
			View.FindViewById (Resource.Id.cancel).Click += (sender, e) => Dismiss ();

			HandleInvalidateUi ();
		}

		void HandleInvalidateUi ()
		{
			name.Text = controller.Name;
			name.Error = controller.NameError ? GetString (Resource.String.required_field) : null;
			View.FindViewById (Resource.Id.ok).Enabled = !controller.IsBusy;
			View.FindViewById (Resource.Id.cancel).Enabled = !controller.IsBusy;
			if (controller.IsComplete)
				DismissAllowingStateLoss ();
		}
	}
}