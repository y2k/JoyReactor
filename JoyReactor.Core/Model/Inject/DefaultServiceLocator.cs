using Autofac;
using JoyReactor.Core.Model.Image;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Model.Web.Parser;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Database;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;

namespace JoyReactor.Core.Model.Inject
{
	public class DefaultServiceLocator : ServiceLocatorImplBase
	{
		private IContainer locator;

		public DefaultServiceLocator (params Module[] platformModule)
		{
			var b = new ContainerBuilder ();

			b.RegisterModule (new DefaultModule ());
			foreach (var s in platformModule) {
				b.RegisterModule (s);
			}

			locator = b.Build ();
		}

		#region implemented abstract members of ServiceLocatorImplBase

		protected override object DoGetInstance (Type serviceType, string key)
		{
			return locator.Resolve (serviceType);
		}

		protected override IEnumerable<object> DoGetAllInstances (Type serviceType)
		{
			throw new NotImplementedException ();
		}

		#endregion

		#region Inner classes

		private class DefaultModule : Module
		{
			protected override void Load (ContainerBuilder b)
			{
				b.RegisterType<Log.DumpLogger> ().As<Log.ILogger> ();
				b.RegisterType<WebDownloader> ().As<IWebDownloader> ();
				b.Register (_ => MainDb.Instance).As<ISQLiteConnection> ();

				b.RegisterType<ReactorParser> ().As<SiteParser> ();
				b.RegisterType<Chan2Parser> ().As<SiteParser> ();
				b.RegisterType<Chan4Parser> ().As<SiteParser> ();
				b.RegisterType<Chan7Parser> ().As<SiteParser> ();

				b.RegisterType<DefaultDiskCache> ().As<IDiskCache> ();
				b.RegisterType<MemoryCache> ().As<IMemoryCache> ();
				b.RegisterType<PostCollectionModel> ().As<IPostCollectionModel> ();
				b.RegisterType<ImageModel> ().As<IImageModel> ();
				b.RegisterType<TagCollectionModel> ().As<ITagCollectionModel> ();
				b.RegisterType<ProfileModel> ().As<IProfileModel> ();
				b.RegisterType<PostModel> ().As<IPostModel> ().SingleInstance ();
			}
		}

		#endregion
	}
}