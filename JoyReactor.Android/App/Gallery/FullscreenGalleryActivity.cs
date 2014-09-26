
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
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model;

namespace JoyReactor.Android.App.Gallery
{
	[Activity (Label = "FullscreenGalleryActivity")]			
	public class FullscreenGalleryActivity : BaseActivity
	{
		private ViewPager pager;
		private IPostModel model = ServiceLocator.Current.GetInstance<IPostModel> ();

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			// Create your application here
			SetContentView (pager = new ViewPager (this));

			pager.PageSelected += (sender, e) => {
				//
			};
		}
	}
}