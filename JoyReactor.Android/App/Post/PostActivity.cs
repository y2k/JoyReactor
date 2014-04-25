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
using JoyReactor.Android.App.Base;
using Android.Support.V4.App;
using Android.Support.V4.View;

namespace JoyReactor.Android.App.Post
{
	[Activity (Label = "PostActivity")]			
	public class PostActivity : BaseActivity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.ActivityPost);

			var pager = FindViewById<ViewPager> (Resource.Id.Pager);
			pager.Adapter = new Adapter (SupportFragmentManager);
		}

		private class Adapter : FragmentStatePagerAdapter 
		{
			public Adapter(global::Android.Support.V4.App.FragmentManager fm) : base(fm) {}

			#region implemented abstract members of PagerAdapter

			public override int Count {
				get {
					return 1000;
				}
			}

			#endregion

			#region implemented abstract members of FragmentStatePagerAdapter

			public override global::Android.Support.V4.App.Fragment GetItem (int position)
			{
				var args = new Bundle ();
				args.PutString ("position", "" + position);
				var f = new TestFragment ();
				f.Arguments = args;
				return f;
			}

			#endregion
		}

		public class TestFragment : BaseFragment
		{
			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				var v = new TextView (Activity);
				v.Text = "Position = " + Arguments.GetString ("position");
				return v;
			}
		}
	}
}