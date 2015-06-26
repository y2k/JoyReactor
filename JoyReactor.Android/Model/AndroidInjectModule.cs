using System;
using Autofac;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Helper;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinAndroid;

namespace JoyReactor.Android.Model
{
    public class AndroidInjectModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LogcatLogger>().As<Log.ILogger>();
            builder.RegisterType<SQLitePlatformAndroid>().As<ISQLitePlatform>();
            builder.RegisterType<Platform>().As<IPlatform>();
        }

        public class Platform : IPlatform
        {
            public Version GetVersion()
            {
                var app = App.App.Instance;
                var info = app.PackageManager.GetPackageInfo(app.PackageName, 0);
                return new Version(info.VersionName);
            }
        }
    }
}