using Autofac;
using PCLStorage;
using SQLite.Net.Interop;
using System;
using XamarinCommons.Image;

namespace JoyReactor.Models
{
    public class Wp8InjectModule : Module
    {
        #region implemented abstract members of NinjectModule

        protected override void Load(ContainerBuilder b)
        {
            b.RegisterType<SQLite.Net.Platform.WinRT.SQLitePlatformWinRT>().As<ISQLitePlatform>();
            b.RegisterType<PathImageDecoder>().As<ImageDecoder>();
        }

        #endregion
    }

    public class PathImageDecoder : ImageDecoder
    {
        public override object Decode(IFile file)
        {
            return new PathImage { PathUri = new Uri(file.Path) };
        }

        public override int GetImageSize(object commonImage)
        {
            return 0;
        }
    }
}