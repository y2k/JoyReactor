using System;
using Android.App;
using Android.Runtime;
using JoyReactor.Android.Model;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.App
{
    [Application (Theme = "@style/AppTheme")]
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