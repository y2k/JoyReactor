using System;
using System.Collections.Generic;
using Autofac;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Messages;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Profiles;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using Refractored.Xam.Settings;
using SQLite.Net;

namespace JoyReactor.Core.Model
{
    public class DefaultServiceLocator : ServiceLocatorImplBase
    {
        private IContainer locator;

        public DefaultServiceLocator(params Module[] platformModule)
        {
            var b = new ContainerBuilder();

            b.RegisterModule(new DefaultModule());
            foreach (var s in platformModule)
            {
                b.RegisterModule(s);
            }

            locator = b.Build();
        }

        #region implemented abstract members of ServiceLocatorImplBase

        protected override object DoGetInstance(Type serviceType, string key)
        {
            return locator.Resolve(serviceType);
        }

        protected override IEnumerable<object> DoGetAllInstances(Type serviceType)
        {
            throw new NotImplementedException();
        }

        #endregion

        #region Inner classes

        class DefaultModule : Module
        {
            protected override void Load(ContainerBuilder b)
            {
                b.RegisterType<Log.DumpLogger>().As<Log.ILogger>();
                b.RegisterType<WebDownloader>().As<IWebDownloader>();
                b.Register(_ => SQLiteConnectionFactory.Create()).As<SQLiteConnection>();

                b.Register(_ => CrossSettings.Current).AsSelf();

                b.RegisterType<ProfileService>().As<IProfileService>();

                b.RegisterType<SQLiteMessageStorage>().As<MessageService.IStorage>();
                b.RegisterType<SQLiteMessageStorage>().As<MessageFetcher.IStorage>();

                b.RegisterType<MessageService>().As<MessageThreadsViewModel.IMessageService>();
                b.RegisterType<MessageService>().As<MessagesViewModel.IMessageService>();

                b.RegisterType<SQLiteAuthStorage>().As<ProfileService.IAuthStorage>();
                b.RegisterType<SQLiteAuthStorage>().As<ReactorMessageParser.IAuthStorage>();
                b.RegisterType<SQLiteAuthStorage>().As<JoyReactorProvider.IAuthStorage>();

                b.RegisterType<SQLiteStorage>().As<FeedService.IStorage>();
                b.RegisterType<SQLiteStorage>().As<JoyReactorProvider.IStorage>();
                b.RegisterType<SQLiteStorage>().As<PostService.IStorage>();

                b.RegisterType<PostService>().As<CreateTagViewModel.IPostService>();

                b.RegisterType<OrderedListStorage>().As<JoyReactorProvider.IListStorage>();

                b.RegisterInstance(MemoryStorage.Intance).As<TagCollectionModel.Storage>();
            }
        }

        #endregion
    }
}