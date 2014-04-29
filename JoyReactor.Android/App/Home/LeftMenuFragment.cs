using JoyReactor.Android.App.Base;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using Android.Content;
using Android.Graphics;
using System;
using JoyReactor.Core;
using JoyReactor.Android.App.Base.Commands;

namespace JoyReactor.Android.App.Home
{
	public class LeftMenuFragment : BaseFragment
	{
		private ListView list;
		private Adapter adapter;
		private ISubscriptionCollectionModel model = InjectService.Instance.Get<ISubscriptionCollectionModel> ();

		public async override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter = new Adapter(Activity);
			adapter.Clear ();
			adapter.AddAll(await model.GetMainSubscriptionsAsync ());

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

		public class Adapter : ArrayAdapter<Tag>
		{
			private IImageModel iModel = InjectService.Instance.Get<IImageModel> ();

			public Adapter(Context context) : base(context, 0) { }

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
					convertView = View.Inflate (parent.Context, Resource.Layout.ItemSubscription, null);

				var i = GetItem (position);
				convertView.FindViewById<TextView> (Resource.Id.title).Text = i.Title;

				var iv = convertView.FindViewById<ImageView> (Resource.Id.icon);
				iModel.Load (iv, new Uri (i.BestImage), 0, s => iv.SetImageBitmap ((Bitmap)s.Image));

				convertView.FindViewById (Resource.Id.group).Visibility = ViewStates.Gone;

				return convertView;
			}
		}
	}
}