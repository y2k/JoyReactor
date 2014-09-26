using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Support.V4.App;
using JoyReactor.Android.App.Gallery;

namespace JoyReactor.Android.App.Base
{
	public class BaseActivity : FragmentActivity
	{
		public const string Arg1 = "arg1";
		public const string Arg2 = "arg2";
		public const string Arg3 = "arg3";
		public const string Arg4 = "arg4";

		protected static Intent NewIntent (Type activityType, params object[] args)
		{
			var t = new Intent (App.Instance, activityType);
			for (int i = 0; i < args.Length; i++) {
				var a = args [i];
				var key = "arg" + (i + 1);

				if (a is string)
					t.PutExtra (key, (string)a);
				else if (a is int)
					t.PutExtra (key, (int)a);
				else if (a is long)
					t.PutExtra (key, (long)a);
			}
			return t;
		}

		public void NavigateToGallery (int postId)
		{
			StartActivity (NewIntent (typeof(GalleryActivity), postId));
		}

		public void NavigateToFullscreenGallery (int postId, int initPosition)
		{
			StartActivity (NewIntent (typeof(FullscreenGalleryActivity), postId, initPosition));
		}
	}
}