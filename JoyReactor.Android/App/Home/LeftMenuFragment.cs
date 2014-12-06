using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.App.Base.Commands;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
	public class LeftMenuFragment : BaseFragment
	{
		static MenuHeader[] Headers = {
			new MenuHeader { Title = Resource.String.feed, ListId = ID.Factory.New (ID.IdConst.ReactorGood) },
			new MenuHeader { Title = Resource.String.favorite, ListId = ID.Factory.New (ID.IdConst.ReactorFavorite) },
		};

		ListView list;
		Adapter adapter;
		TagCollectionModel model = new TagCollectionModel();

		public async override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);

			list.Adapter = adapter = new Adapter (Activity);
			adapter.Clear ();
			adapter.AddAll (await model.GetMainSubscriptionsAsync ());

			list.ItemClick += (sender, e) => { 
				var id = e.Position < Headers.Length 
					? Headers [e.Position].ListId 
					: ID.Parser (adapter.GetItem (e.Position - Headers.Length).TagId);
				new ChangeSubscriptionCommand (id).Execute ();
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
			public Adapter (Context context) : base (context, 0)
			{
			}

			public override int Count {
				get { return base.Count + Headers.Length; }
			}

			public override View GetView (int position, View convertView, ViewGroup parent)
			{
				convertView = convertView ?? View.Inflate (parent.Context, Resource.Layout.ItemSubscription, null);

				if (position < Headers.Length) {
					convertView.FindViewById<TextView> (Resource.Id.title).SetText (Headers [position].Title);
					convertView.FindViewById<WebImageView> (Resource.Id.icon).ImageSource = null;
				} else {
					var i = GetItem (position - Headers.Length);
					convertView.FindViewById<TextView> (Resource.Id.title).Text = i.Title;
					convertView.FindViewById<WebImageView> (Resource.Id.icon).ImageSource = i.BestImage;
				}

				convertView.FindViewById (Resource.Id.group).Visibility = ViewStates.Gone;
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