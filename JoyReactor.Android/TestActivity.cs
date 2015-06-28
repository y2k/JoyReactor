using Android.App;
using Android.OS;
using JoyReactor.Android.Widget;
using System.IO;
using System.Net;
using Android.Widget;

namespace JoyReactor.Android
{
    [Activity(Label = "Large image decoder", MainLauncher = false)]
    public class TestActivity : Activity
    {
        LargeImageViewer viewer;

        protected override async void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);
            SetContentView(viewer = new LargeImageViewer(this, null));

            var testImage = GetFileStreamPath("fj38fhs-testImage.jpeg").AbsolutePath;
            if (!File.Exists(testImage))
            {
                Toast.MakeText(this, "Start image download", ToastLength.Short).Show();
                var client = new WebClient();
                await client.DownloadFileTaskAsync(
                    "http://img0.joyreactor.cc/pics/post/-1885843.jpeg", 
                    testImage);
                Toast.MakeText(this, "Image downloaded", ToastLength.Long).Show();
            }

            viewer.SetImage(testImage);
        }
    }
}