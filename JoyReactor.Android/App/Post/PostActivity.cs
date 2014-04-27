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
using Android.Util;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using Ninject;
using JoyReactor.Core;

namespace JoyReactor.Android.App.Post
{
	[Activity (Label = "PostActivity")]			
	public class PostActivity : BaseActivity
	{
		private IPostCollectionModel model = InjectService.Instance.Get<IPostCollectionModel>();

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.ActivityPost);

			var pager = FindViewById<ViewPager> (Resource.Id.Pager);
			int count = model.GetCount (new ID ());
			pager.Adapter = new Adapter (count, SupportFragmentManager);
		}

		private class Adapter : FragmentStatePagerAdapter 
		{
			private int count;

			public Adapter(int count, global::Android.Support.V4.App.FragmentManager fm) : base(fm) {
				this.count = count;
			}

			#region implemented abstract members of PagerAdapter

			public override int GetItemPosition (Java.Lang.Object @object)
			{
				return PagerAdapter.PositionNone;
			}

			public override int Count { get { return count; } }

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
			public override void OnSaveInstanceState (Bundle outState)
			{
				base.OnSaveInstanceState (outState);
				outState.PutString ("zzz", "" + new Random ().Next ());
			}

//			public override void OnActivityCreated (Bundle savedInstanceState)
//			{
//				base.OnActivityCreated (savedInstanceState);
//				Log.Info ("PostActivity", "State = " + savedInstanceState);
//			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				var v = new TextView (Activity);
				v.Text = "Position = " + Arguments.GetString ("position") + ", State = " + savedInstanceState;
				return v;
			}
		}
	}
}