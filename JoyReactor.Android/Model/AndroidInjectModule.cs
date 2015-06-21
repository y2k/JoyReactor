using Autofac;
using JoyReactor.Core.Model.Helper;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinAndroid;

namespace JoyReactor.Android.Model
{
	public class AndroidInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<LogcatLogger> ().As<Log.ILogger>();
			builder.RegisterType<SQLitePlatformAndroid> ().As<ISQLitePlatform>();
		}

		#endregion
	}
}