using Android.App;
using Android.OS;
using Android.Content;

namespace JoyReactor.AndroidTv
{
	[Activity (Label = "@string/app_name", MainLauncher = true, Icon = "@drawable/ic_launcher", Theme = "@style/Theme.Leanback")]
	[IntentFilter(new [] { Intent.ActionMain }, Categories = new [] { Intent.CategoryLeanbackLauncher })]
	public class MainActivity : Activity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.Main);
			if (bundle == null)
				FragmentManager.BeginTransaction ().Add (Resource.Id.container, new MainFragment ()).Commit ();
		}
	}
}