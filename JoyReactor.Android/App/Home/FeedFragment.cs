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
using Com.Android.EX.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		private StaggeredGridView list;
		private ProgressBar progress;
		private FeedAdapter adapter;

		private IPostCollectionModel model = InjectService.Locator.GetInstance<IPostCollectionModel>();

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			list.Adapter = adapter = new FeedAdapter (Activity);
			list.SetItemMargin((int)(4 * Resources.DisplayMetrics.Density));

			ReloadList (ID.ReactorGood);
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.FragmentFeed, null);
			list = v.FindViewById<StaggeredGridView> (Resource.Id.List);
			progress = v.FindViewById<ProgressBar> (Resource.Id.Progress);
			return v;
		}

		public override void OnResume ()
		{
			base.OnResume ();
			ChangeSubscriptionCommand.Register (this, ReloadList);
		}

		public override void OnPause ()
		{
			base.OnPause ();
			ChangeSubscriptionCommand.Unregister (this);
		}

		private async void ReloadList (ID id)
		{
			adapter.ListId = id;

			adapter.Clear ();
			var result = await model.GetPostsAsync (id, SyncFlags.First);
			adapter.AddAll (result);
			progress.Visibility = ViewStates.Gone;
		}
	}
}