using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.Widget;
using Android.Util;
using Android.Views;
using Android.Widget;
using Com.Android.EX.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;
using System.Threading.Tasks;

namespace JoyReactor.Android.App.Home
{
	public class OldFeedFragment : BaseFragment
	{
		private StaggeredGridView list;
		private FeedAdapter adapter;

		private IPostCollectionModel model = ServiceLocator.Current.GetInstance<IPostCollectionModel> ();
		private SwipeRefreshLayout refresher;

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;

			list.Adapter = adapter = new FeedAdapter (Activity);

			if (savedInstanceState == null)
				ChangeCurrentSubscription (ID.Factory.New (ID.IdConst.ReactorGood));
			OnPostCollectionChanged (null, adapter.ListId);

			refresher.Refresh += async (s, e) => {
				await model.SyncTask (adapter.ListId, SyncFlags.First);
				refresher.Refreshing = false;
				OnPostCollectionChanged(null, adapter.ListId);
			};

			AddLifeTimeEvent (
				() => ChangeSubscriptionCommand.Register (this, ChangeCurrentSubscription), 
				() => ChangeSubscriptionCommand.Unregister (this));

			adapter.ClickMore += async (sender, e) => {
				refresher.Refreshing = true;
				await model.SyncTask (adapter.ListId,SyncFlags.Next);
				refresher.Refreshing = false;
				OnPostCollectionChanged(null, adapter.ListId);
			};
		}

		private async void OnPostCollectionChanged (object sender, ID e)
		{
			var newPosts = await model.GetPostsAsync (adapter.ListId);
			adapter.ReplaceAll (newPosts);
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.fragment_feed, null);
			list = v.FindViewById<StaggeredGridView> (Resource.Id.List);
			list.SetItemMargin ((int)(4 * Resources.DisplayMetrics.Density));
			refresher = v.FindViewById<SwipeRefreshLayout> (Resource.Id.refresher);
			return v;
		}

		private async void ChangeCurrentSubscription (ID id)
		{
			refresher.Refreshing = true;
			list.Adapter = adapter = new FeedAdapter (Activity);
			await model.SyncTask (adapter.ListId = id, SyncFlags.First);
			refresher.Refreshing = false;
			OnPostCollectionChanged (null, id);
		}
	}
}