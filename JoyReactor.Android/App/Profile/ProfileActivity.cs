using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Android.App.Base;
using Android.Widget;

namespace JoyReactor.Android.App.Profile
{
	[Activity (Label = "ProfileActivity")]			
	public class ProfileActivity : BaseActivity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			if (bundle == null) {
				SupportFragmentManager.BeginTransaction ().Add (global::Android.Resource.Id.Content, new ProfileFragment ()).Commit ();
			}
		}

		public class ProfileFragment : BaseFragment
		{
			private IProfileModel model = InjectService.Instance.Get<IProfileModel>();

			private ViewAnimator animator;
			private EditText username;
			private EditText password;
			private TextView profileUsername;
			private TextView profileRating;

			public async override void OnActivityCreated (Bundle savedInstanceState)
			{
				base.OnActivityCreated (savedInstanceState);

				View.FindViewById(Resource.Id.login).Click += async (sender, e) => {
					animator.DisplayedChild = 0;
					await model.LoginAsync(username.Text, password.Text);
					Activity.Recreate();
				};
				View.FindViewById (Resource.Id.logout).Click += async (sender, e) => {
					animator.DisplayedChild = 0;
					await model.LogoutAsync();
					Activity.Recreate();
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