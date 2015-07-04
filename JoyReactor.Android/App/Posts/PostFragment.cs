using System.Collections.Specialized;
using Android.OS;
using Android.Support.V4.Widget;
using Android.Support.V7.Widget;
using Android.Views;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Android.App.Posts
{
    public class PostFragment : BaseFragment
    {
        PostViewModel viewmodel = new PostViewModel();
        RecyclerView list;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;
            viewmodel.Initialize(Arguments.GetInt(Arg1));
        }

        public override void OnActivityCreated(Bundle savedInstanceState)
        {
            base.OnActivityCreated(savedInstanceState);
            list.SetAdapter(new Adapter { viewmodel = viewmodel });
            viewmodel.Comments.CollectionChanged += HandleCollectionChanged;
        }

        public override void OnDestroyView()
        {
            base.OnDestroyView();
            viewmodel.Comments.CollectionChanged -= HandleCollectionChanged;
        }

        void HandleCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            list.GetAdapter().NotifyDataSetChanged();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.fragment_post_2, container, false);
            list = view.FindViewById<RecyclerView>(Resource.Id.list);
            list.SetLayoutManager(new LinearLayoutManager(Activity));

            var refresher = view.FindViewById<SwipeRefreshLayout>(Resource.Id.refresher);
            Bindings
                .Add(viewmodel, () => viewmodel.IsBusy)
                .WhenSourceChanges(() => refresher.Refreshing = viewmodel.IsBusy);
            refresher.Refresh += (sender, e) => viewmodel.ReloadCommand.Execute(null);

            return view;
        }

        public static PostFragment NewFragment(int postId)
        {
            return NewFragment<PostFragment>(postId);
        }

        internal class Adapter : RecyclerView.Adapter
        {
            public PostViewModel viewmodel;

            public override int GetItemViewType(int position)
            {
                return position == 0 ? 0 : 1;
            }

            public override void OnBindViewHolder(RecyclerView.ViewHolder holder, int position)
            {
                ((PostViewHolder)holder).OnBindViewHolder(position - 1);
            }

            public override RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
            {
                return viewType == 0
                    ? (RecyclerView.ViewHolder)new HeaderRow(parent, viewmodel)
                    : new CommentRow(parent, viewmodel);
            }

            public override int ItemCount
            {
                get { return viewmodel.Comments.Count + 1; }
            }

            internal interface PostViewHolder
            {
                void OnBindViewHolder(int position);
            }
        }
    }
}