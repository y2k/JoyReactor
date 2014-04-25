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
using Android.Support.V4.View;
using Android.Graphics;
using Android.Support.V4.App;
using JoyReactor.Android.App.Home;
using JoyReactor.Core.Model.Inject;
using Ninject;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App
{
	[Activity (Label = "JoyReactor v2", MainLauncher = true)]			
	public class HomeActivity : BaseActivity
	{
		private ViewPager pager;

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.ActivityHome);

			pager = FindViewById<ViewPager> (Resource.Id.Pager);
			pager.Adapter = new Adapter (SupportFragmentManager);
			pager.CurrentItem = 1;
		}

		protected override void OnResume ()
		{
			base.OnResume ();
			ChangeSubscriptionCommand.Register (this, s => pager.CurrentItem = 1);
		}

		protected override void OnPause ()
		{
			base.OnPause ();
			ChangeSubscriptionCommand.Unregister (this);
		}
	}

	public class Adapter : FragmentPagerAdapter
	{
		public Adapter(global::Android.Support.V4.App.FragmentManager fm) : base(fm) { }

		#region implemented abstract members of PagerAdapter

		public override int Count {
			get {
				return 3;
			}
		}

		public override float GetPageWidth (int position)
		{
			return position == 1 ? 1 : 0.7f;
		}

		#endregion

		#region implemented abstract members of FragmentPagerAdapter

		public override global::Android.Support.V4.App.Fragment GetItem (int position)
		{
			if (position == 0)
				return new LeftMenuFragment ();
			if (position == 1)
				return new FeedFragment ();
			return new EmptyFragment ();
		}

		#endregion
	}

	public class EmptyFragment : BaseFragment
	{
		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var view = new View (container.Context);
			view.SetBackgroundColor (Color.Green);
			return view;
		}
	}
}