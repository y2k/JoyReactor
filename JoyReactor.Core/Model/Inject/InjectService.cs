using System;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Web;
using Ninject;
using Ninject.Modules;
using System.Collections.Generic;
using JoyReactor.Core.Model.Image;

namespace JoyReactor.Core.Model.Inject
{
	public class InjectService 
	{
		public static IKernel Instance { get ; private set; }

//		private InjectService(params INinjectModule[] modules) : base(modules) { }

		public static void Initialize(IKernel baseKernel, params INinjectModule[] modules) 
		{
			var m = new List<INinjectModule>();
			m.Add (new DefaultModule ());
			m.AddRange (modules);

//			Instance = new InjectService (m.ToArray());
			baseKernel.Load (m);
			Instance = baseKernel;
		}

		private class DefaultModule : NinjectModule
		{
			#region implemented abstract members of NinjectModule

			public override void Load ()
			{
				Bind<ISiteParser> ().To<ReactorParser> ();
				Bind<IWebDownloader> ().To<WebDownloader> ();
				Bind<IDiskCache>().To<DefaultDiskCache>().InSingletonScope();
				Bind<IMemoryCache> ().To<MemoryCache> ().InSingletonScope();

				Bind<IPostCollectionModel> ().To<PostCollectionModel> ().InSingletonScope();
				Bind<IImageModel> ().To<ImageModel> ().InSingletonScope();
				Bind<ISubscriptionCollectionModel> ().To<SubscriptionCollectionModel> ().InSingletonScope();
			}

			#endregion
		}
	}
}