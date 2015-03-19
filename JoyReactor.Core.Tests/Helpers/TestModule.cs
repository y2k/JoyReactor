using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Threading.Tasks;
using Autofac;
using JoyReactor.Core.Model.Messages;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Tests.Xam.Pluging.Settings;
using Refractored.Xam.Settings.Abstractions;
using SQLite.Net;
using XamarinCommons.Image;

namespace JoyReactor.Core.Tests.Helpers
{
    public class TestModule : Module
    {
        Action<ContainerBuilder> builderCallback;

        public TestModule() { }

        public TestModule(Action<ContainerBuilder> builderCallback) {
            this.builderCallback = builderCallback; 
        }

        protected override void Load(ContainerBuilder b)
        {
            b.RegisterType<MockWebDownloader>().As<IWebDownloader>().SingleInstance();
            b.RegisterInstance(MockSQLiteConnection.Create()).As<SQLiteConnection>();
            b.RegisterInstance(new MockSettings()).As<ISettings>();

            b.RegisterType<StubImageDecoder>().As<ImageDecoder>();
            b.RegisterType<MockAuthStorage>().As<ReactorMessageParser.IAuthStorage>();
//            b.RegisterType<MockAuthStorage>().As<JoyReactorSiteApi.IAuthStorage>();

            builderCallback?.Invoke(b);
        }

        class MockAuthStorage : ReactorMessageParser.IAuthStorage
        {
            public Task<IDictionary<string, string>> GetCookiesAsync()
            {
                return Task.FromResult<IDictionary<string, string>>(new Dictionary<string, string>());
            }

            public Task<string> GetCurrentUserNameAsync()
            {
                return Task.FromResult("mykie78");
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