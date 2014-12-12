using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Controllers;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App.Home
{
	public class RightMenuFragment : BaseFragment
	{
		ListView list;
		Adapter adapter;

		TagInformationViewModel viewModel = new TagInformationViewModel ();

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
		}

		public override void OnStart ()
		{
			base.OnStart ();
			ChangeSubscriptionCommand.Register (this, s => viewModel.ChangeCurrentTag (s));
			viewModel.Items.CollectionChanged += HandleCollectionChanged;
		}

		public override void OnStop ()
		{
			base.OnStop ();
			ChangeSubscriptionCommand.Unregister (this);
			viewModel.Items.CollectionChanged -= HandleCollectionChanged;
		}

		void HandleCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			adapter.NotifyDataSetChanged ();
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter = new Adapter { Source = viewModel.Items };
			list.ItemClick += (sender, e) => { 
// TODO:
//				var id = ID.Parser (adapter.GetItem (e.Position).TagId);
//				new ChangeSubscriptionCommand (id).Execute ();
			};
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.Inflate (Resource.Layout.FragmentLeftMenu, null);
			list = v.FindViewById<ListView> (Resource.Id.List);
			return v;
		}

		public class Adapter : BaseAdapter<TagInformationViewModel.ItemViewModel>
		{
			ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();

			public IList<TagInformationViewModel.ItemViewModel> Source { get; set; }

			public override long GetItemId (int position)
			{
				return position;
			}

			public override int Count {
				get {
					return Source?.Count ?? 0;
				}
			}

			public override TagInformationViewModel.ItemViewModel this [int index] {
				get {
					return Source [index];
				}
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
					convertView = View.Inflate (parent.Context, Resource.Layout.ItemSubscription, null);

				var i = Source [position];
				convertView.FindViewById<TextView> (Resource.Id.title).Text = i.Title;

				var iv = convertView.FindViewById<ImageView> (Resource.Id.icon);
				if (i.Image == null)
					iv.SetImageBitmap (null);
				else
					iModel.Load (iv, new Uri (i.Image), 0, s => iv.SetImageBitmap ((Bitmap)s));

				convertView.FindViewById (Resource.Id.group).Visibility = ViewStates.Gone;

				return convertView;
			}
		}
	}
}