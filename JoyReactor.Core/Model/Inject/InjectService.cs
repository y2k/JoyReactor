using System;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Web;
using System.Collections.Generic;
using JoyReactor.Core.Model.Image;
using Autofac;
using Autofac.Core;
using Autofac.Util;
using Autofac.Features;
using Autofac.Builder;
using System.Linq;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Inject
{
    public class InjectService : IServiceLocator
    {
        [Obsolete]
        public static IContainer Instance { get { return Current; } }

        [Obsolete]
        public static IContainer Current { get { return ((InjectService)Locator).container; } }

        public static IServiceLocator Locator { get; private set; }

        private IContainer container;

        private InjectService(IContainer container)
        {
            this.container = container;
        }

        public static void Initialize(params IModule[] modules)
        {
            if (Locator == null)
            {
                var b = new ContainerBuilder();
                b.RegisterModule(new DefaultModule());
                foreach (var m in modules)
                    b.RegisterModule(m);

                Locator = new InjectService(b.Build());
            }
        }

        private class DefaultModule : Module
        {
            protected override void Load(ContainerBuilder b)
            {
                b.RegisterType<WebDownloader>().As<IWebDownloader>();
                b.RegisterType<ReactorParser>().As<ISiteParser>();
                b.RegisterType<DefaultDiskCache>().As<IDiskCache>();
                b.RegisterType<MemoryCache>().As<IMemoryCache>();
                b.RegisterType<PostCollectionModel>().As<IPostCollectionModel>();
                b.RegisterType<ImageModel>().As<IImageModel>();
                b.RegisterType<TagCollectionModel>().As<ITagCollectionModel>();
                b.RegisterType<ProfileModel>().As<IProfileModel>();
                b.RegisterType<PostModel>().As<IPostModel>().SingleInstance();
            }
        }

        #region Implementation IServiceLocator

        public IEnumerable<TService> GetAllInstances<TService>()
        {
            throw new NotImplementedException();
        }

        public IEnumerable<object> GetAllInstances(Type serviceType)
        {
            throw new NotImplementedException();
        }

        public TService GetInstance<TService>(string key)
        {
            throw new NotImplementedException();
        }

        public TService GetInstance<TService>()
        {
            return container.Resolve<TService>();
        }

        public object GetInstance(Type serviceType, string key)
        {
            throw new NotImplementedException();
        }

        public object GetInstance(Type serviceType)
        {
            throw new NotImplementedException();
        }

        public object GetService(Type serviceType)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}