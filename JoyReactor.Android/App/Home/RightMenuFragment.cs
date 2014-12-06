using System;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App.Home
{
	public class RightMenuFragment : BaseFragment
	{
		ListView list;
		Adapter adapter;
		TagCollectionModel model = new TagCollectionModel();

		public override void OnResume ()
		{
			base.OnResume ();
			ChangeSubscriptionCommand.Register (this, async s => {
				adapter.Clear ();
				adapter.AddAll(await model.GetTagLinkedTagsAsync(s));
			});
		}

		public override void OnPause ()
		{
			base.OnPause ();
			ChangeSubscriptionCommand.Unregister (this);
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter = new Adapter(Activity);

			list.ItemClick += (sender, e) => { 
				var id = ID.Parser(adapter.GetItem(e.Position).TagId);
				new ChangeSubscriptionCommand(id).Execute();
			};
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.Inflate (Resource.Layout.FragmentLeftMenu, null);
			list = v.FindViewById<ListView> (Resource.Id.List);
			return v;
		}

		public class Adapter : ArrayAdapter<TagLinkedTag>
		{
			ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();

			public Adapter(Context context) : base(context, 0) { }

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
					convertView = View.Inflate (parent.Context, Resource.Layout.ItemSubscription, null);

				var i = GetItem (position);
				convertView.FindViewById<TextView> (Resource.Id.title).Text = i.Title;

				var iv = convertView.FindViewById<ImageView> (Resource.Id.icon);
				if (i.Image == null) iv.SetImageBitmap (null);
				else iModel.Load (iv, new Uri (i.Image), 0, s => iv.SetImageBitmap ((Bitmap)s));

				convertView.FindViewById (Resource.Id.group).Visibility = ViewStates.Gone;

				return convertView;
			}
		}
	}
}