using System.Collections.Specialized;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Home
{
	public class LeftMenuFragment : BaseFragment
	{
		ListView list;
		Adapter adapter;
		TagsViewModel viewModel;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
			viewModel = new TagsViewModel ();

			adapter = new Adapter (this);
			viewModel.Tags.CollectionChanged += HandleCollectionChanged;
		}

		public override void OnDestroy ()
		{
			base.OnDestroy ();
			viewModel.Cleanup ();
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter;
            list.ItemClick += (sender, e) => { 
                var id = e.Position < 1 ? null : viewModel.Tags [e.Position - 1].TagId;
                MessengerInstance.Send (new TagsViewModel.SelectTagMessage { Id = id });
            };
		}

		void HandleCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			adapter.NotifyDataSetChanged ();
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.Inflate (Resource.Layout.fragment_left_menu, null);
			list = v.FindViewById<ListView> (Resource.Id.list);
			return v;
		}

		public class Adapter : ArrayAdapter<TagsViewModel.TagItemViewModel>
		{
			LeftMenuFragment fragment;

			public Adapter (LeftMenuFragment fragment) : base (fragment.Activity, 0)
			{
				this.fragment = fragment;
			}

			public override int Count {
				get { return fragment.viewModel.Tags.Count + 1; }
			}

			public override int ViewTypeCount { 
				get { return 2; }
			}

			public override int GetItemViewType (int position)
			{
				return position == 0 ? 0 : 1;
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				if (GetItemViewType (position) == 0) {
					convertView = convertView ?? View.Inflate(parent.Context, Resource.Layout.layout_subscriptions_header, null);
                    convertView.FindViewById(Resource.Id.selectFeatured).Click
                        += (sender, e) => fragment.MessengerInstance.Send (
                            new TagsViewModel.SelectTagMessage { Id = ID.Factory.New (ID.IdConst.ReactorGood) });
                    convertView.FindViewById(Resource.Id.selectFavorite).Click
                        += (sender, e) => fragment.MessengerInstance.Send (
                            new TagsViewModel.SelectTagMessage { Id = ID.Factory.New (ID.IdConst.ReactorFavorite) });
				} else {
					convertView = convertView ?? View.Inflate (parent.Context, Resource.Layout.item_subscription, null);
					var i = fragment.viewModel.Tags [position - 1];
					convertView.FindViewById<TextView> (Resource.Id.title).Text = i.Title;
					convertView.FindViewById<WebImageView> (Resource.Id.icon).ImageSource = i.Image;
				}
				return convertView;
			}
		}

		class MenuHeader
		{
			internal int Title { get; set; }

			internal ID ListId { get; set; }
		}
	}
}