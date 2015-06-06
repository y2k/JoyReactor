using Autofac;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinIOS;
using UIKit;
using XamarinCommons.Image;

namespace JoyReactor.iOS.Platform
{
    public class IosInjectModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<ImageDecoderImpl>().As<ImageDecoder>();
            builder.RegisterType<SQLitePlatformIOS>().As<ISQLitePlatform>();
        }

        public class ImageDecoderImpl : ImageDecoder
        {
            public override int GetImageSize(object commonImage)
            {
                var img = commonImage as UIImage;
                return (int)(img.Size.Width * img.Size.Height * 4);
            }

            public override object Decode(PCLStorage.IFile file)
            {
                return UIImage.FromFile(file.Path);
            }
        }
    }
}