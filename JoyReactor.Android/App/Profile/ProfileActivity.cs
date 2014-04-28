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
using Ninject;
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

			public async override void OnActivityCreated (Bundle savedInstanceState)
			{
				base.OnActivityCreated (savedInstanceState);

				View.FindViewById(Resource.Id.login).Click += async (sender, e) => {
					animator.DisplayedChild = 0;
					await model.LoginAsync(username.Text, password.Text);
					Activity.Recreate();
				};

				var t = await model.GetCurrentProfileAsync ();
				if (t != null) t.ToString ();
				animator.DisplayedChild = 1;
			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				var v = inflater.Inflate (Resource.Layout.fragment_profile, null);
				animator = v.FindViewById<ViewAnimator> (Resource.Id.animator);
				username = v.FindViewById<EditText> (Resource.Id.username);
				password = v.FindViewById<EditText> (Resource.Id.password);
				return v;
			}
		}
	}
}