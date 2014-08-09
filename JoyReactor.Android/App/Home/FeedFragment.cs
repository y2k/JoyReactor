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
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.App.Home
{
	public class FeedFragment : BaseFragment
	{
		private StaggeredGridView list;
		private ProgressBar progress;
		private FeedAdapter adapter;
		private bool loading;

		private IPostCollectionModel model = ServiceLocator.Current.GetInstance<IPostCollectionModel> ();

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			list.SetItemMargin ((int)(4 * Resources.DisplayMetrics.Density));

			ReloadList (ID.Factory.New (ID.IdConst.ReactorGood));
			AddLifeTimeEvent (() => ChangeSubscriptionCommand.Register (this, ReloadList), () => ChangeSubscriptionCommand.Unregister (this));
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			var v = inflater.Inflate (Resource.Layout.FragmentFeed, null);
			list = v.FindViewById<StaggeredGridView> (Resource.Id.List);
			progress = v.FindViewById<ProgressBar> (Resource.Id.Progress);
			return v;
		}

		private async void ReloadList (ID id)
		{
			ReCreateAdapter ();
			adapter.ListId = id;

			var result = await model.GetListAsync (id, SyncFlags.First);
			adapter.AddAll (result);
			progress.Visibility = ViewStates.Gone;
		}

		private void ReCreateAdapter ()
		{
			list.Adapter = adapter = new FeedAdapter (Activity);
			adapter.ClickMore += async (sender, e) => {
			
				if (!loading) {
					loading = true;
					var result = await model.GetPostsAsync (adapter.ListId, SyncFlags.Next);
					loading = false;

					adapter.Clear ();
					adapter.AddAll (result);
				}
			
			};
		}
	}
}