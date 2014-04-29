using System;
using JoyReactor.Core.Model.Image;
using SQLite.Net.Platform.XamarinAndroid;
using SQLite.Net.Interop;
using JoyReactor.Core.Model.Database;
using Autofac;

namespace JoyReactor.Android.Model
{
	public class AndroidInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<BitmapImageDecoder> ().As<IImageDecoder> ();
			builder.RegisterType<AndroidSQLitePlatformGetter> ().As<ISQLitePlatfromGetter> ();
		}

		#endregion
	}
}