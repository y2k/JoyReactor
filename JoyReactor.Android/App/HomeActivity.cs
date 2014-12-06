using Android.App;
using Android.Graphics;
using Android.OS;
using Android.Support.V4.App;
using Android.Support.V4.View;
using Android.Views;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;
using JoyReactor.Android.App.Home;

namespace JoyReactor.Android.App
{
	[Activity (Label = "@string/app_name", MainLauncher = true)]			
	public class HomeActivity : BaseActivity
	{
		ViewPager pager;

		protected override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			SetContentView (Resource.Layout.activity_home);

			var view = FindViewById (global::Android.Resource.Id.Content);
			view.ToString ();

			pager = FindViewById<ViewPager> (Resource.Id.pager);
			pager.Adapter = new Adapter (SupportFragmentManager);
			pager.PageSelected += (s, e) => SupportActionBar.SetDisplayHomeAsUpEnabled (e.Position > 0);

			pager.CurrentItem = 1;
		}

		public override bool OnCreateOptionsMenu (IMenu menu)
		{
			MenuInflater.Inflate (Resource.Menu.home, menu);
			return true;
		}

		public override bool OnOptionsItemSelected (IMenuItem item)
		{
			switch (item.ItemId) {
			case global::Android.Resource.Id.Home:
				pager.CurrentItem = 0;
				return true;
			case Resource.Id.profile:
				StartActivity (typeof(ProfileActivity));
				return true;
			case Resource.Id.addTag:
				new CreateTagDialog ().Show (SupportFragmentManager, null);
				return true;
			}
			return base.OnOptionsItemSelected (item);
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
		public Adapter (global::Android.Support.V4.App.FragmentManager fm) : base (fm)
		{
		}

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
			return new RightMenuFragment ();
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