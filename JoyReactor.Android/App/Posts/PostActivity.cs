using Android.App;
using Android.Content;
using Android.OS;
using JoyReactor.Android.App.Base;

namespace JoyReactor.Android.App.Posts
{
    [Activity(
        Label = "@string/post_acitivty",
        ScreenOrientation = global::Android.Content.PM.ScreenOrientation.Portrait)]
    [MetaData("android.support.PARENT_ACTIVITY", Value = "net.itwister.joyreactor2.HomeActivity")]
    public class PostActivity : BaseActivity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.layout_activity_container);

            if (savedInstanceState == null)
            {
                int id = Intent.GetIntExtra(Arg1, 0);
                SupportFragmentManager
					.BeginTransaction()
                    .Add(Resource.Id.container, PostFragment.NewFragment(id))
					.Commit();
            }
        }

        public static Intent NewIntent(int id)
        {
            return BaseActivity.NewIntent(typeof(PostActivity), id);
        }
    }
}