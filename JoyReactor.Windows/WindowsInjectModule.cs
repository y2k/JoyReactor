using Autofac;
using JoyReactor.Core.Model;
using PCLStorage;
using SQLite.Net.Interop;
using SQLite.Net.Platform.WinRT;
using System;
using Windows.UI.Xaml.Media.Imaging;
using XamarinCommons.Image;

namespace JoyReactor.Windows
{
    class WindowsInjectModule : Module
    {
        #region implemented abstract members of NinjectModule

        protected override void Load(ContainerBuilder b)
        {
            b.RegisterType<SQLitePlatformWinRT>().As<ISQLitePlatform>();
            b.RegisterType<PathImageDecoder>().As<ImageDecoder>();
        }

        #endregion
    }

    public class PathImageDecoder : ImageDecoder, ImageRequest.IImageConverter
    {
        public T Convert<T>(object metaImage)
        {
            if (metaImage == null) return default(T);
            if (typeof(T) == typeof(BitmapImage)) return (T)(object)new BitmapImage(((PathImage)metaImage).PathUri);
            throw new ArgumentException("Not supported type of image " + typeof(T));
        }

        public override object Decode(IFile file)
        {
            return new PathImage { PathUri = new Uri(file.Path) };
        }

        public override int GetImageSize(object commonImage)
        {
            return 0;
        }
    }

    class PathImage : IDisposable
    {
        public Uri PathUri { get; set; }

        public void Dispose()
        {
            // Ignore
        }
    }
}
