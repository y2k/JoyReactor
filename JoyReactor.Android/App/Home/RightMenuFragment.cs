using System.Collections.Generic;
using System.Collections.Specialized;
using Android.Graphics;
using Android.OS;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Home
{
    public class RightMenuFragment : BaseFragment
    {
        ListView list;
        Adapter adapter;
        TagInformationViewModel viewModel = new TagInformationViewModel();

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;
            viewModel.Items.CollectionChanged += HandleCollectionChanged;
            viewModel.Initialize();
        }

        void HandleCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            adapter?.NotifyDataSetChanged();
        }

        public override void OnActivityCreated(Bundle savedInstanceState)
        {
            base.OnActivityCreated(savedInstanceState);

            list.Adapter = adapter = new Adapter { Source = viewModel.Items };
            list.ItemClick += (sender, e) =>
            { 
// TODO:
//				var id = ID.Parser (adapter.GetItem (e.Position).TagId);
//				new ChangeSubscriptionCommand (id).Execute ();
            };
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.Inflate(Resource.Layout.FragmentLeftMenu, null);
            list = v.FindViewById<ListView>(Resource.Id.List);
            return v;
        }

        public class Adapter : BaseAdapter<TagInformationViewModel.ItemViewModel>
        {
            public IList<TagInformationViewModel.ItemViewModel> Source { get; set; }

            public override long GetItemId(int position)
            {
                return position;
            }

            public override int Count
            {
                get
                {
                    return Source == null ? 0 : Source.Count;
                }
            }

            public override TagInformationViewModel.ItemViewModel this [int index]
            {
                get
                {
                    return Source[index];
                }
            }

            public override View GetView(int position, View convertView, ViewGroup parent)
            {
                if (convertView == null)
                    convertView = View.Inflate(parent.Context, Resource.Layout.ItemSubscription, null);

                var i = Source[position];
                convertView.FindViewById<TextView>(Resource.Id.title).Text = i.Title;

                var iv = convertView.FindViewById<ImageView>(Resource.Id.icon);
//				if (i.Image == null)
//					iv.SetImageBitmap (null);
//				else
//					iModel.Load (iv, new Uri (i.Image), 0, s => iv.SetImageBitmap ((Bitmap)s));
                new ImageRequest()
                    .SetToken(iv)
                    .SetUrl(i.Image)
                    .Into<Bitmap>(iv.SetImageBitmap);

                convertView.FindViewById(Resource.Id.group).Visibility = ViewStates.Gone;
                return convertView;
            }
        }
    }
}