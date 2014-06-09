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
		private static MenuHeader[] Headers = new MenuHeader[] {
			new MenuHeader { Title = "Feed", ListId = ID.REACTOR_GOOD },
			new MenuHeader { Title = "Favorite", ListId = ID.ReactorFavorite },
		};

		private ListView list;
		private Adapter adapter;
		private ITagCollectionModel model = InjectService.Locator.GetInstance<ITagCollectionModel> ();

		public async override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter = new Adapter(Activity);
			adapter.Clear ();
			adapter.AddAll(await model.GetMainSubscriptionsAsync ());

			list.ItemClick += (sender, e) => { 
				var id = e.Position < Headers.Length 
					? Headers[e.Position].ListId 
					: ID.Parser(adapter.GetItem(e.Position - Headers.Length).TagId);
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
			private IImageModel iModel = InjectService.Locator.GetInstance<IImageModel> ();

			public Adapter(Context context) : base(context, 0) { }

			public override int Count {
				get {
					return base.Count + Headers.Length;
				}
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				convertView = convertView ?? View.Inflate (parent.Context, Resource.Layout.ItemSubscription, null);

				if (position < Headers.Length) {
					// TODO
					convertView.FindViewById<TextView> (Resource.Id.title).Text = Headers [position].Title;
				} else {
					var i = GetItem (position - Headers.Length);
					convertView.FindViewById<TextView> (Resource.Id.title).Text = i.Title;

					var iv = convertView.FindViewById<ImageView> (Resource.Id.icon);
					if (i.BestImage == null) iv.SetImageBitmap (null);
					else iModel.Load (iv, new Uri (i.BestImage), 0, s => iv.SetImageBitmap ((Bitmap)s.Image));
				}

				convertView.FindViewById (Resource.Id.group).Visibility = ViewStates.Gone;
				return convertView;
			}
		}

		class MenuHeader {

			internal string Title { get; set; }
			internal ID ListId { get; set; }
		}
	}
}