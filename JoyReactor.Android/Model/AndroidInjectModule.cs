using System;
using JoyReactor.Core.Model.Image;
using JoyReactor.Core.Model.Database;
using Autofac;
using Community.SQLite;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.Droid;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;

namespace JoyReactor.Android.Model
{
	public class AndroidInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<BitmapImageDecoder> ().As<IImageDecoder> ();
			builder.RegisterType<MvxDroidSQLiteConnectionFactory> ().As<ISQLiteConnectionFactory>();
		}

		#endregion
	}
}