using System;
using JoyReactor.Core.Model.Image;
using JoyReactor.Core.Model.Database;
using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.WindowsPhone;
using JoyReactor.WP.ViewModel;

namespace JoyReactor.WP.Model
{
    public class Wp8InjectModule : Module
    {
        #region implemented abstract members of NinjectModule

        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<BitmapImageDecoder>().As<IImageDecoder>();
            builder.RegisterType<MvxWindowsPhoneSQLiteConnectionFactory>().As<ISQLiteConnectionFactory>();

            builder.RegisterType<MainViewModel>().AsSelf().SingleInstance();
            builder.RegisterType<ProfileViewModel>().AsSelf().SingleInstance();
            builder.RegisterType<PostViewModel>().AsSelf().SingleInstance();
            builder.RegisterType<AttachmentsViewModel>().AsSelf().SingleInstance();
        }

        #endregion
    }
}