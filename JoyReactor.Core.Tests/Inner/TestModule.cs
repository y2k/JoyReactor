using Autofac;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Tests.Xam.Pluging.Settings;
using Refractored.Xam.Settings.Abstractions;
using SQLite.Net;
using System.Drawing;
using System.IO;
using XamarinCommons.Image;

namespace JoyReactor.Core.Tests.Inner
{
    public class TestModule : Module
    {
        protected override void Load(ContainerBuilder b)
        {
            b.RegisterType<MockWebDownloader>().As<IWebDownloader>().SingleInstance();
            b.RegisterInstance(MockSQLiteConnection.Create()).As<SQLiteConnection>();
            b.RegisterInstance(new MockSettings()).As<ISettings>();

            b.RegisterType<StubImageDecoder>().As<ImageDecoder>();
        }

        class StubImageDecoder : ImageDecoder
        {
            public override object DecoderStream(Stream stream)
            {
                return Bitmap.FromStream(stream);
            }

            public override int GetImageSize(object commonImage)
            {
                var bmp = (Bitmap)commonImage;
                return bmp == null ? 0 : bmp.Width * bmp.Height * 4;
            }
        }
    }
}