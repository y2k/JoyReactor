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
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using Ninject;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Android.App.Base;
using Com.Android.EX.Widget;
using JoyReactor.Core;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		private StaggeredGridView list;
		private ProgressBar progress;

		private IPostCollectionModel model = InjectService.Instance.Get<IPostCollectionModel>();

		public override async void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			var adapter = new FeedAdapter(Activity);
			list.Adapter = adapter;

			// TODO
			var result = await model.GetPostsAsync (new ID { Site = ID.SiteParser.JoyReactor, Type = ID.TagType.Good }, SyncFlags.First);
			adapter.Clear ();
			adapter.AddAll (result);
			progress.Visibility = ViewStates.Gone;

			// TODO
			list.SetItemMargin((int)(4 * Resources.DisplayMetrics.Density));
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.FragmentFeed, null);
			list = v.FindViewById<StaggeredGridView> (Resource.Id.List);
			progress = v.FindViewById<ProgressBar> (Resource.Id.Progress);
			return v;
		}
	}
}