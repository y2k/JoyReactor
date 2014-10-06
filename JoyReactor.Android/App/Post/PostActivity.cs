using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.App;
using Android.Support.V4.View;
using Android.Util;
using Android.Views;
using Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using Microsoft.Practices.ServiceLocation;
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