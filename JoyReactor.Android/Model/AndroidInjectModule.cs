using System;
using Ninject.Modules;
using JoyReactor.Core.Model.Image;
using SQLite.Net.Platform.XamarinAndroid;
using SQLite.Net.Interop;
using JoyReactor.Core.Model.Database;

namespace JoyReactor.Android.Model
{
	public class AndroidInjectModule : NinjectModule
	{
		#region implemented abstract members of NinjectModule

		public override void Load ()
		{
			Bind<IImageDecoder>().To<BitmapImageDecoder> ();
//			Bind<ISQLitePlatform> ().To<SQLitePlatformAndroid> ();
			Bind<ISQLitePlatfromGetter>().To<AndroidSQLitePlatformGetter>();
		}

		#endregion
	}
}