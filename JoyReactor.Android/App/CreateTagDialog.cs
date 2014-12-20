﻿using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.ViewModels;
using GalaSoft.MvvmLight.Helpers;

namespace JoyReactor.Android.App
{
	public class CreateTagDialog : BaseDialogFragment
	{
		CreateTagViewModel controller = new CreateTagViewModel ();
		EditText name;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
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

			View.FindViewById (Resource.Id.cancel).Click += (sender, e) => Dismiss ();

			controller.SetBinding (() => controller.Name, name, () => name.Text);
			controller
				.SetBinding (() => controller.NameError, name, () => name.Error)
				.ConvertSourceToTarget (s => s ? GetString (Resource.String.required_field) : null);

			View.FindViewById (Resource.Id.ok).SetCommand ("Click", controller.CreateCommand);
		}
	}
}