using Autofac;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinIOS;
using XamarinCommons.Image;

namespace JoyReactor.Ios.Model
{
	public class IosInjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<BitmapImageDecoder> ().As<ImageDecoder> ();
			builder.RegisterType<SQLitePlatformIOS> ().As<ISQLitePlatform> ();
		}

		#endregion
	}
}