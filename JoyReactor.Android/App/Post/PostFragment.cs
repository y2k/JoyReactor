using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using Android.Support.V4.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;

namespace JoyReactor.Android.App.Post
{
	public class PostFragment : BaseFragment
	{
		private IPostModel model = InjectService.Locator.GetInstance<IPostModel> ();

		public async override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			var p = await model.GetPostAsync (null, Arguments.GetInt ("pos"));
			var coms = await model.GetTopCommentsAsync (p.Id, 5);

			// Create your fragment here
		}

		public static PostFragment NewFragment(int position) {
			var a = new Bundle ();
			a.PutInt ("pos", position);
			return new PostFragment { Arguments = a };
		}
	}
}