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
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Database;

namespace JoyReactor.Core.Tests.Helpers
{
    public class TestModule : Module
    {
        Action<ContainerBuilder> builderCallback;

        public TestModule()
        {
        }

        public TestModule(Action<ContainerBuilder> builderCallback)
        {
            this.builderCallback = builderCallback; 
        }

        protected override void Load(ContainerBuilder b)
        {
            //            b.RegisterInstance(MockSQLiteConnection.Create()).As<SQLiteConnection>();
            var connection = MockSQLiteConnection.Create();
            b.RegisterInstance(connection).As<SQLiteConnection>();
            b.RegisterInstance(new AsyncSQLiteConnection(connection)).As<AsyncSQLiteConnection>();

            b.RegisterType<MockWebDownloader>().As<WebDownloader>().SingleInstance();
            b.RegisterInstance(new MockSettings()).As<ISettings>();

            b.RegisterType<StubImageDecoder>().As<ImageDecoder>();
            b.RegisterType<MockAuthStorage>().As<ReactorMessageParser.IAuthStorage>();
            b.RegisterType<MockAuthStorage>().As<IProviderAuthStorage>();

            builderCallback?.Invoke(b);
        }

        class MockAuthStorage : ReactorMessageParser.IAuthStorage, IProviderAuthStorage
        {
            public Task<IDictionary<string, string>> GetCookiesAsync()
            {
                return Task.FromResult<IDictionary<string, string>>(new Dictionary<string, string>());
            }

            public Task<string> GetCurrentUserNameAsync()
            {
                return Task.FromResult("mykie78");
            }

            public Task SaveCookieToDatabaseAsync(string username, IDictionary<string, string> cookies)
            {
                return Task.FromResult(false);
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