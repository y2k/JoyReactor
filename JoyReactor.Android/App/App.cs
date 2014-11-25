using System;
using Android.App;
using Android.Runtime;
using JoyReactor.Core.Model.Inject;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.Model;

namespace JoyReactor.Android.App
{
	[Application (Theme = "@style/Theme.AppCompat.Light")]
	public class App : Application
	{
		public static Application Instance { get; private set; }

		public App (IntPtr handle, JniHandleOwnership transfer) : base (handle, transfer)
		{
		}

		public override void OnCreate ()
		{
			base.OnCreate ();

			Instance = this;
			var locator = new DefaultServiceLocator (new AndroidInjectModule ());
			ServiceLocator.SetLocatorProvider (() => locator);
		}
	}
}