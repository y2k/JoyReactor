using System.IO;
using System;
using Android.App;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Autofac;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net.Interop;
using SQLite.Net.Platform.XamarinAndroid;
using XamarinCommons.Image;
using Android.Graphics.Drawables;
using JoyReactor.Core.Model.Web;
using System.Threading.Tasks;
using System.Threading;

namespace JoyReactor.AndroidTv
{
    [Activity(Label = "@string/app_name", MainLauncher = true, Icon = "@drawable/ic_launcher")]
    [IntentFilter(new [] { Intent.ActionMain }, Categories = new [] { Intent.CategoryLeanbackLauncher })]
    public class MainActivity : Activity
    {
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(Resource.Layout.Main);
            if (bundle == null)
                FragmentManager.BeginTransaction().Add(Resource.Id.container, new MainFragment()).Commit();
        }


        #region App initialize

        static MainActivity()
        {
            var locator = new DefaultServiceLocator(new AndroidInjectModule());
            ServiceLocator.SetLocatorProvider(() => locator);
        }

        class AndroidInjectModule : Module
        {
            protected override void Load(ContainerBuilder builder)
            {
                builder.RegisterType<BitmapImageDecoder>().As<ImageDecoder>();
                builder.RegisterType<SQLitePlatformAndroid>().As<ISQLitePlatform>();
                builder.RegisterType<MultiWebDownloader>().As<IWebDownloader>();
            }

            class MultiWebDownloader : IWebDownloader {

                IWebDownloader baseDownloader = new WebDownloader();
                static long GlobalDelay;

                public async Task<WebResponse> ExecuteAsync(Uri uri, RequestParams reqParams = null)
                {
                    var delaySec = Math.Max(0, Interlocked.Increment(ref GlobalDelay));
                    try {
                        await Task.Delay(TimeSpan.FromSeconds(2 * delaySec));
                        return await baseDownloader.ExecuteAsync(uri, reqParams);
                    } finally {
                        Interlocked.Decrement(ref GlobalDelay);
                    }
                }
            }

            class BitmapImageDecoder : ImageDecoder, ImageRequest.IImageConverter
            {
                #region implemented abstract members of ImageDecoder

                public override int GetImageSize(object commonImage)
                {
                    return commonImage == null ? 0 : ((Bitmap)commonImage).ByteCount;
                }

                public override object DecoderStream(Stream stream)
                {
                    return BitmapFactory.DecodeStream(stream);
                }

                #endregion

                #region IImageConverter implementation

                public T Convert<T>(object metaImage)
                {
                    if (metaImage == null)
                        return default(T);
                    if (typeof(T) == typeof(Bitmap))
                        return (T)metaImage;
                    if (typeof(T) == typeof(BitmapDrawable))
                        return (T)(object)new BitmapDrawable((Bitmap)metaImage);
                    throw new ArgumentException();
                }

                #endregion
            }
        }

        #endregion
    }
}