using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Java.IO;
using Java.Lang;
using Java.Util.Zip;
using File = Java.IO.File;
using Path = System.IO.Path;
using StringBuilder = System.Text.StringBuilder;
using Uri = Android.Net.Uri;

namespace JoyReactor.Android.Model
{
    class FeedbackController
    {
        readonly Activity activity;

        internal FeedbackController(Activity activity)
        {
            this.activity = activity;
        }

        internal async void Send()
        {
            var path = Path.Combine("" + activity.GetExternalFilesDir(null), "system_info_" + Guid.NewGuid() + ".zip");
            using (var file = System.IO.File.Create(path))
            {
                using (var zip = new ZipOutputStream(file))
                {
                    zip.SetMethod(ZipOutputStream.Deflated);
                    zip.SetLevel(5);

                    await zip.PutNextEntryAsync(new ZipEntry("screenshot.jpeg"));
                    await TakeScreenshot(zip);
                    zip.CloseEntry();
                    await zip.PutNextEntryAsync(new ZipEntry("system_info.txt"));
                    await zip.WriteAsync(Encoding.UTF8.GetBytes(new SystemInformation(activity).GetInformation()));
                    zip.CloseEntry();
                    zip.Finish();
                }
            }
            activity.StartActivity(CreateEmailIntent(new File(path)));
        }

        async Task TakeScreenshot(OutputStream stream)
        {
            var root = activity.Window.DecorView;
            var bitmap = Bitmap.CreateBitmap(root.Width, root.Height, Bitmap.Config.Argb8888);
            var canvas = new Canvas(bitmap);
            root.Draw(canvas);

            var memory = new MemoryStream();
            await bitmap.CompressAsync(Bitmap.CompressFormat.Jpeg, 90, memory);
            await stream.WriteAsync(memory.ToArray());
        }

        Intent CreateEmailIntent(File screenshot)
        {
            Intent emailItent = new Intent(Intent.ActionSend)
                .SetType("application/zip")
                .PutExtra(Intent.ExtraEmail, new [] { "joyreactor.feedbacks@gmail.com" })
                .PutExtra(Intent.ExtraSubject, activity.GetString(Resource.String.write_feedback))
                .PutExtra(Intent.ExtraStream, Uri.FromFile(screenshot));

            var prefApps = new []
            { 
                "com.google.android.apps.inbox", 
                "com.google.android.gm",
                "com.android.email"
            };
            var package = activity.PackageManager
				.QueryIntentActivities(emailItent, 0)
				.Join(prefApps, s => s.ActivityInfo.PackageName, s => s, (a, b) => a)
				.Select(s => s.ActivityInfo.PackageName)
				.FirstOrDefault();
            emailItent.SetPackage(package);
            return emailItent;
        }

        class SystemInformation
        {
            StringBuilder description = new StringBuilder();
            Activity activity;

            public SystemInformation(Activity activity)
            {
                this.activity = activity;
            }

            public string GetInformation()
            {
                var runtime = Runtime.GetRuntime();
                Add("Data", DateTime.Now);
                Add("VersionCode", activity.PackageManager.GetPackageInfo(activity.PackageName, 0).VersionCode);
                Add("VersionName", activity.PackageManager.GetPackageInfo(activity.PackageName, 0).VersionName);
                Add("Activity", activity.GetType().Name);
                Add("AvailableProcessors", runtime.AvailableProcessors());
                Add("FreeMemory", runtime.FreeMemory());
                Add("MaxMemory", runtime.MaxMemory());
                Add("TotalMemory", runtime.TotalMemory());
                description.AppendLine();
                foreach (var prop in typeof(Build.VERSION).GetProperties(BindingFlags.Static | BindingFlags.Public))
                    Add(prop.Name, prop.GetValue(null));
                description.AppendLine();
                foreach (var prop in typeof(Build).GetProperties(BindingFlags.Static | BindingFlags.Public))
                    Add(prop.Name, prop.GetValue(null));
                return description.ToString();
            }

            void Add(string key, object value)
            {
                description.Append(key + " = " + value + "\n");
            }
        }
    }
}