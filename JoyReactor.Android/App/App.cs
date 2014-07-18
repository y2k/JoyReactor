using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Android.Model;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.App
{
	[Application]
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
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new AndroidInjectModule ()));
		}
	}
}