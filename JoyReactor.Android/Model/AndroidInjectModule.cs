using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.Droid;
using JoyReactor.Core.Model.Helper;
using XamarinCommons.Image;

namespace JoyReactor.Android.Model
{
	public class AndroidInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<LogcatLogger> ().As<Log.ILogger>();
			builder.RegisterType<BitmapImageDecoder> ().As<IImageDecoder> ();
			builder.RegisterType<MvxDroidSQLiteConnectionFactory> ().As<ISQLiteConnectionFactory>();
		}

		#endregion
	}
}