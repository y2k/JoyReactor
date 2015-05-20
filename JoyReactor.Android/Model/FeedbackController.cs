using System.IO;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.Graphics;
using Android.Net;
using Java.IO;
using File = Java.IO.File;
using Images = Android.Provider.MediaStore.Images;

namespace JoyReactor.Android.Model
{
    class FeedbackController
    {
        Activity activity;

        internal FeedbackController(Activity activity)
        {
            this.activity = activity;
        }

        internal async void Send() {
            var screenshot = await TakeScreenshot();

            Intent emailItent = new Intent(Intent.ActionSend)
                .SetType("image/jpeg")
                .PutExtra(Intent.ExtraEmail, "feedback@fake-mail.net")
                .PutExtra(Intent.ExtraSubject, "Email subject")
                .PutExtra(Intent.ExtraText, "Email text")
                .PutExtra(Intent.ExtraStream, Uri.FromFile(screenshot));
            activity.StartActivity(emailItent);
        }

        async Task<File> TakeScreenshot()
        {
            var root = activity.FindViewById(global::Android.Resource.Id.Content);
            var bitmap = Bitmap.CreateBitmap(root.Width, root.Height, Bitmap.Config.Argb8888);
            var canvas = new Canvas(bitmap);
            root.Draw(canvas);

            var target = File.CreateTempFile("feedback_screenshot_", ".jpeg", activity.GetExternalFilesDir(null));
            using (var stream = new FileStream(target.AbsolutePath, FileMode.Create))
            {
                await bitmap.CompressAsync(Bitmap.CompressFormat.Jpeg, 90, stream);
            }
            return target;
        }
    }
}