using System;
using JoyReactor.Core.Model.Image;
using JoyReactor.Core.Model.Database;
using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.WindowsPhone;

namespace JoyReactor.WP.Model
{
    public class Wp8InjectModule : Module
    {
        #region implemented abstract members of NinjectModule

        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<BitmapImageDecoder>().As<IImageDecoder>();
            builder.RegisterType<MvxWindowsPhoneSQLiteConnectionFactory>().As<ISQLiteConnectionFactory>();
        }

        #endregion
    }
}