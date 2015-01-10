using System.Drawing;
using System.IO;
using Autofac;
using JoyReactor.Core.Model.Web;
using Refractored.Xam.Settings.Abstractions;
using SQLite.Net;
using XamarinCommons.Image;
using JoyReactor.Core.Tests.Xam.Pluging.Settings;
using JoyReactor.Core.Model.Messages;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.Helpers
{
    public class TestModule : Module
    {
        protected override void Load(ContainerBuilder b)
        {
            b.RegisterType<MockWebDownloader>().As<IWebDownloader>().SingleInstance();
            b.RegisterInstance(MockSQLiteConnection.Create()).As<SQLiteConnection>();
            b.RegisterInstance(new MockSettings()).As<ISettings>();

            b.RegisterType<StubImageDecoder>().As<ImageDecoder>();
            b.RegisterType<MockAuthStorage>().As<ReactorMessageParser.IAuthStorage>();
        }

        class MockAuthStorage : ReactorMessageParser.IAuthStorage
        {
            public Task<IDictionary<string, string>> GetCookiesAsync()
            {
                return Task.FromResult<IDictionary<string, string>>(new Dictionary<string, string>());
            }
        }

        class StubImageDecoder : ImageDecoder
        {
            public override object DecoderStream(Stream stream)
            {
                return Image.FromStream(stream);
            }

            public override int GetImageSize(object commonImage)
            {
                var bmp = (Bitmap)commonImage;
                return bmp == null ? 0 : bmp.Width * bmp.Height * 4;
            }
        }
    }
}