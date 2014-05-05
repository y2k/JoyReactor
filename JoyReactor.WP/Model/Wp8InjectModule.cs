using System;
using JoyReactor.Core.Model.Image;
using SQLite.Net.Interop;
using JoyReactor.Core.Model.Database;
using Autofac;

namespace JoyReactor.WP.Model
{
	public class Wp8InjectModule : Module
	{
		#region implemented abstract members of NinjectModule

		protected override void Load (ContainerBuilder builder)
		{
			builder.RegisterType<BitmapImageDecoder> ().As<IImageDecoder> ();
			builder.RegisterType<Wp8SQLitePlatformGetter> ().As<ISQLitePlatfromGetter> ();
		}

		#endregion
	}
}