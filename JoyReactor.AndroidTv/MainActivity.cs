using System.IO;
using Android.App;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Autofac;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinAndroid;
using XamarinCommons.Image;

namespace JoyReactor.AndroidTv
{
	[Activity (Label = "@string/app_name", MainLauncher = true, Icon = "@drawable/ic_launcher")]
	[IntentFilter(new [] { Intent.ActionMain }, Categories = new [] { Intent.CategoryLeanbackLauncher })]
	public class MainActivity : Activity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.Main);
			if (bundle == null)
				FragmentManager.BeginTransaction ().Add (Resource.Id.container, new MainFragment ()).Commit ();
		}


        #region App initialize

        static MainActivity()
        {
            var locator = new DefaultServiceLocator(new AndroidInjectModule());
            ServiceLocator.SetLocatorProvider(() => locator);
        }

        class AndroidInjectModule : Module
        {
            protected override void Load(ContainerBuilder builder)
            {
                builder.RegisterType<BitmapImageDecoder>().As<ImageDecoder>();
                builder.RegisterType<SQLitePlatformAndroid>().As<ISQLitePlatform>();
            }

            class BitmapImageDecoder : ImageDecoder
            {
                public override int GetImageSize(object commonImage)
                {
                    return commonImage == null ? 0 : ((Bitmap)commonImage).ByteCount;
                }

                public override object DecoderStream(Stream stream)
                {
                    return BitmapFactory.DecodeStream(stream);
                }
            }
        }

        #endregion
	}
}