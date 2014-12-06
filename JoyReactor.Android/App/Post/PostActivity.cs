using Android.App;
using Android.Content;
using Android.OS;
using JoyReactor.Android.App.Base;

namespace JoyReactor.Android.App.Post
{
	[Activity (Label = "@string/post_acitivty", ParentActivity = typeof(HomeActivity))]
	public class PostActivity : BaseActivity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.activity_post);
			SupportActionBar.SetDisplayHomeAsUpEnabled (true);

			if (bundle == null) {
				int id = Intent.GetIntExtra (Arg1, 0);
				SupportFragmentManager
					.BeginTransaction ()
					.Add (Resource.Id.content_frame, PostFragment.NewFragment (id))
					.Commit ();
			}
		}

		public static Intent NewIntent (int id)
		{
			return BaseActivity.NewIntent (typeof(PostActivity), id);
		}
	}
}