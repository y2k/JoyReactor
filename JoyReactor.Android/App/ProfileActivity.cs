using Android.App;
using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Android.App.Base;

namespace JoyReactor.Android.App
{
	[Activity (Label = "@string/profile", ParentActivity = typeof(HomeActivity))]			
	public class ProfileActivity : BaseActivity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentViewForFragment ();
			SupportActionBar.SetDisplayHomeAsUpEnabled (true);
			if (bundle == null)
				SetRootFragment (new ProfileFragment ());
		}

		public class ProfileFragment : BaseFragment
		{
			ProfileOperation model = new ProfileOperation ();

			ViewAnimator animator;
			EditText username;
			EditText password;
			TextView profileUsername;
			TextView profileRating;

			public async override void OnActivityCreated (Bundle savedInstanceState)
			{
				base.OnActivityCreated (savedInstanceState);

				View.FindViewById (Resource.Id.login).Click += async (sender, e) => {
					animator.DisplayedChild = 0;
					await model.LoginAsync (username.Text, password.Text);
//					await model.GetCurrentProfileAsync ();
					StartActivity (new Intent (Activity, typeof(HomeActivity)).AddFlags (ActivityFlags.ClearTop | ActivityFlags.TaskOnHome));
				};
				View.FindViewById (Resource.Id.logout).Click += async (sender, e) => {
					animator.DisplayedChild = 0;
					await model.LogoutAsync ();
					StartActivity (new Intent (Activity, typeof(HomeActivity)).AddFlags (ActivityFlags.ClearTop | ActivityFlags.TaskOnHome));
				};

				var t = await model.GetCurrentProfileAsync ();
				if (t == null) {
					// TODO
					animator.DisplayedChild = 1;
				} else {
					// TODO
					animator.DisplayedChild = 2;
					profileUsername.Text = "Username = " + t.Username;
					profileRating.Text = "Rating = " + t.Rating;
				}
			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				var v = inflater.Inflate (Resource.Layout.fragment_profile, null);
				animator = v.FindViewById<ViewAnimator> (Resource.Id.animator);
				username = v.FindViewById<EditText> (Resource.Id.username);
				password = v.FindViewById<EditText> (Resource.Id.password);
				profileUsername = v.FindViewById<TextView> (Resource.Id.profileUsername);
				profileRating = v.FindViewById<TextView> (Resource.Id.profileRating);
				return v;
			}
		}
	}
}