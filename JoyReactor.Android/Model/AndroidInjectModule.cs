using Autofac;
using JoyReactor.Core.Model.Helper;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinAndroid;
using XamarinCommons.Image;

namespace JoyReactor.Android.Model
{
	public class AndroidInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<LogcatLogger> ().As<Log.ILogger>();
			builder.RegisterType<BitmapImageDecoder> ().As<ImageDecoder> ();
			builder.RegisterType<SQLitePlatformAndroid> ().As<ISQLitePlatform>();
		}

		#endregion
	}
}