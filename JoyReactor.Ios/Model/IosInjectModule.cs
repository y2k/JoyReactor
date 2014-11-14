using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.Touch;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using XamarinCommons.Image;

namespace JoyReactor.Ios.Model
{
	public class IosInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<BitmapImageDecoder> ().As<ImageDecoder> ();
			builder.RegisterType<MvxTouchSQLiteConnectionFactory> ().As<ISQLiteConnectionFactory>();
		}

		#endregion
	}
}