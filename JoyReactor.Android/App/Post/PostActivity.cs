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
	[Activity (Label = "@string/post_acitivty")]			
	public class PostActivity : BaseActivity
	{
		//		public ID ListId { get; private set; }

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
//			SetContentView (Resource.Layout.ActivityPost);

			// var pos = Intent.GetIntExtra ("pos");
//			ListId = ID.DeserializeFromString(Intent.GetStringExtra ("list_id"));

//			var pager = FindViewById<ViewPager> (Resource.Id.Pager);
//			int count = model.GetCount (ID.DeserializeFromString (Intent.GetStringExtra ("list_id")));
//			pager.Adapter = new Adapter (count, SupportFragmentManager);

			if (bundle == null) {
				int id = Intent.GetIntExtra (Arg1, 0);
				SupportFragmentManager
					.BeginTransaction ()
					.Add (global::Android.Resource.Id.Content, PostFragment.NewFragment (id))
					.Commit ();
			}
		}

		//		public static Intent NewIntent(ID listId, int initPosition) {
		//			var i = new Intent (App.Instance, typeof(PostActivity));
		//			i.PutExtra ("list_id", listId.SerializeToString ());
		//			i.PutExtra ("pos", initPosition);
		//			return i;
		//		}


		public static Intent NewIntent (int id)
		{
			return BaseActivity.NewIntent (typeof(PostActivity), id);
		}
	}
}