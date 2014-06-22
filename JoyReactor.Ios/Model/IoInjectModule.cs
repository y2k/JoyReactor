using System;
using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.Touch;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using JoyReactor.Core.Model.Image;

namespace JoyReactor.Ios.Model
{
	public class IoInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<BitmapImageDecoder> ().As<IImageDecoder> ();
			builder.RegisterType<MvxTouchSQLiteConnectionFactory> ().As<ISQLiteConnectionFactory>();
		}

		#endregion
	}
}