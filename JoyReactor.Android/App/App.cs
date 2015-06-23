using System;
using Android.App;
using Android.Runtime;
using JoyReactor.Android.Model;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.App
{
    [Application(Theme = "@style/AppTheme")]
    public class App : Application
    {
        public static Application Instance { get; private set; }

        public App(IntPtr handle, JniHandleOwnership transfer)
            : base(handle, transfer)
        {
        }

        public override void OnCreate()
        {
            base.OnCreate();

            #if DEBUG
            Xamarin.Insights.Initialize("7d0715fea86f4239a3a4d6ce8acbe1871296e8a4", this);
            #else
            Xamarin.Insights.Initialize("1664e416e9def27db9e1d4ddc74255f3537a2c16", this);
            #endif

            Instance = this;
            var locator = new DefaultServiceLocator(new AndroidInjectModule());
            ServiceLocator.SetLocatorProvider(() => locator);
        }
    }
}