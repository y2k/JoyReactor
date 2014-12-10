using System;
using Autofac;
using SQLite.Net.Interop;
using XamarinCommons.Image;

namespace JoyReactor.Models
{
    public class Wp8InjectModule : Module
    {
        #region implemented abstract members of NinjectModule

        protected override void Load(ContainerBuilder b)
        {
#if WINDOWS_PHONE_APP
            // TODO:
#else
            b.RegisterType<SQLite.Net.Platform.WinRT.SQLitePlatformWinRT>().As<ISQLitePlatform>();
#endif
            b.RegisterType<StubImageDecoder>().As<ImageDecoder>();
        }

        #endregion
    }

    public class StubImageDecoder : ImageDecoder
    {
        public override int GetImageSize(object commonImage)
        {
            throw new NotImplementedException();
        }
    }
}