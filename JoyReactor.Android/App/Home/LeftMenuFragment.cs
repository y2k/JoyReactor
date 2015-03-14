using System.Collections.Specialized;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Home
{
    public class LeftMenuFragment : BaseFragment
    {
        static MenuHeader[] Headers =
            {
            new MenuHeader { Title = Resource.String.feed, ListId = ID.Factory.New(ID.IdConst.ReactorGood) },
            new MenuHeader { Title = Resource.String.favorite, ListId = ID.Factory.New(ID.IdConst.ReactorFavorite) },
        };

        ListView list;
        Adapter adapter;

        TagsViewModel viewModel;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;
            viewModel = new TagsViewModel();

            adapter = new Adapter(this);
            viewModel.Tags.CollectionChanged += HandleCollectionChanged;
        }

        public override void OnDestroy()
        {
            base.OnDestroy();
            viewModel.Cleanup();
        }

        public override void OnActivityCreated(Bundle savedInstanceState)
        {
            base.OnActivityCreated(savedInstanceState);

            list.Adapter = adapter;

            list.ItemClick += (sender, e) =>
            { 
                var id = e.Position < Headers.Length 
					? Headers[e.Position].ListId 
					: viewModel.Tags[e.Position - Headers.Length].TagId;
                MessengerInstance.Send(new TagsViewModel.SelectTagMessage { Id = id });
            };
        }

        void HandleCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            adapter.NotifyDataSetChanged();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.Inflate(Resource.Layout.FragmentLeftMenu, null);
            list = v.FindViewById<ListView>(Resource.Id.List);
            return v;
        }

        public class Adapter : ArrayAdapter<TagsViewModel.TagItemViewModel>
        {
            LeftMenuFragment fragment;

            public Adapter(LeftMenuFragment fragment)
                : base(fragment.Activity, 0)
            {
                this.fragment = fragment;
            }

            public override int Count
            {
                get { return fragment.viewModel.Tags.Count + Headers.Length; }
            }

            public override View GetView(int position, View convertView, ViewGroup parent)
            {
                convertView = convertView ?? View.Inflate(parent.Context, Resource.Layout.ItemSubscription, null);

                if (position < Headers.Length)
                {
                    convertView.FindViewById<TextView>(Resource.Id.title).SetText(Headers[position].Title);
                    convertView.FindViewById<WebImageView>(Resource.Id.icon).ImageSource = null;
                }
                else
                {
                    var i = fragment.viewModel.Tags[position - Headers.Length];
                    convertView.FindViewById<TextView>(Resource.Id.title).Text = i.Title;
                    convertView.FindViewById<WebImageView>(Resource.Id.icon).ImageSource = i.Image;
                }

                convertView.FindViewById(Resource.Id.group).Visibility = ViewStates.Gone;
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