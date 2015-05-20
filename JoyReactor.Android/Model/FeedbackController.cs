using System.IO;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.Graphics;
using Android.Net;
using Android.OS;
using Java.IO;
using Java.Lang;
using File = Java.IO.File;

namespace JoyReactor.Android.Model
{
	class FeedbackController
	{
		Activity activity;

		internal FeedbackController (Activity activity)
		{
			this.activity = activity;
		}

		internal async void Send ()
		{
			var screenshot = await TakeScreenshot ();
			activity.StartActivity (CreateEmailIntent (screenshot));
		}

		async Task<File> TakeScreenshot ()
		{
			var root = activity.Window.DecorView;
			var bitmap = Bitmap.CreateBitmap (root.Width, root.Height, Bitmap.Config.Argb8888);
			var canvas = new Canvas (bitmap);
			root.Draw (canvas);

			var target = File.CreateTempFile ("feedback_screenshot_", ".jpeg", activity.GetExternalFilesDir (null));
			using (var stream = new FileStream (target.AbsolutePath, FileMode.Create)) {
				await bitmap.CompressAsync (Bitmap.CompressFormat.Jpeg, 90, stream);
			}
			return target;
		}

		Intent CreateEmailIntent (File screenshot)
		{
			Intent emailItent = new Intent (Intent.ActionSend)
				.SetType ("image/jpeg")
				.PutExtra (Intent.ExtraEmail, new [] { "feedback-y2k@gmail.com" })
				.PutExtra (Intent.ExtraSubject, "[Feedback][JoyReactor]")
				.PutExtra (Intent.ExtraText, GetDescription ())
				.PutExtra (Intent.ExtraStream, Uri.FromFile (screenshot));

			var prefApps = new [] { "com.google.android.apps.inbox", "com.google.android.gm" };
			var package = activity.PackageManager
				.QueryIntentActivities (emailItent, 0)
				.Join (prefApps, s => s.ActivityInfo.PackageName, s => s, (a, b) => a)
				.Select (s => s.ActivityInfo.PackageName)
				.FirstOrDefault ();
			emailItent.SetPackage (package);
			return emailItent;
		}

		string GetDescription ()
		{
			var runtime = Runtime.GetRuntime ();
			var desc = new Description ()
				.Add ("Activity", activity.GetType ().Name)
				.Add ("AvailableProcessors", runtime.AvailableProcessors ())
				.Add ("FreeMemory", runtime.FreeMemory ())
				.Add ("MaxMemory", runtime.MaxMemory ())
				.Add ("TotalMemory", runtime.TotalMemory ());
			foreach (var prop in typeof(Build).GetProperties(BindingFlags.Static | BindingFlags.Public))
				desc.Add (prop.Name, prop.GetValue (null));
			return desc.ToString ();
		}

		class Description
		{
			StringBuilder descBuilder = new StringBuilder ();

			public Description Add (string key, object value)
			{
				descBuilder.Append (key + " = " + value + "\n");
				return this;
			}

			public override string ToString ()
			{
				return "\n\n## system info ##\n\n" + descBuilder + "\n## system info ##\n\n";
			}
		}
	}
}