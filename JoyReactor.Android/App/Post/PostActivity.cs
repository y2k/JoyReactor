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
using JoyReactor.Core;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.App.Post
{
	[Activity (Label = "@string/post_acitivty")]			
	public class PostActivity : BaseActivity
	{
		private IPostCollectionModel model = ServiceLocator.Current.GetInstance<IPostCollectionModel>();

		public ID ListId { get; private set; }

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.ActivityPost);

			// var pos = Intent.GetIntExtra ("pos");
			ListId = ID.DeserializeFromString(Intent.GetStringExtra ("list_id"));

			var pager = FindViewById<ViewPager> (Resource.Id.Pager);
			int count = model.GetCount (ID.DeserializeFromString (Intent.GetStringExtra ("list_id")));
			pager.Adapter = new Adapter (count, SupportFragmentManager);
		}

		public static Intent NewIntent(ID listId, int initPosition) {
			var i = new Intent (App.Instance, typeof(PostActivity));
			i.PutExtra ("list_id", listId.SerializeToString ());
			i.PutExtra ("pos", initPosition);
			return i;
		}

		private class Adapter : FragmentStatePagerAdapter 
		{
			private int count;

			public Adapter(int count, global::Android.Support.V4.App.FragmentManager fm) : base(fm) {
				this.count = count;
			}

			#region implemented abstract members of PagerAdapter

			public override int Count { get { return count; } }

			#endregion

			#region implemented abstract members of FragmentStatePagerAdapter

			public override global::Android.Support.V4.App.Fragment GetItem (int position)
			{
				return PostFragment.NewFragment (position);
			}

			#endregion
		}

//		public class TestFragment : BaseFragment
//		{
//			private string memoryValue;
//
//			public override void OnCreate (Bundle savedInstanceState)
//			{
//				base.OnCreate (savedInstanceState);
//				memoryValue = DateTime.Now.ToString ();
//			}
//
//			public override void OnSaveInstanceState (Bundle outState)
//			{
//				base.OnSaveInstanceState (outState);
//				outState.PutString ("zzz", "" + new Random ().Next ());
//			}
//
//			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//			{
//				var v = new TextView (Activity);
//				v.Text = "Position = " + Arguments.GetString ("position") + ", State = " + savedInstanceState + ", Memory state = " + memoryValue;
//				return v;
//			}
//		}
	}
}