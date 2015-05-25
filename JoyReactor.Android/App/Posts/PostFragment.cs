using System.Collections.Specialized;
using System.Linq;
using Android.OS;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.Model.DTO;
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
            viewmodel.ViewModelParts.CollectionChanged += HandleCollectionChanged;
        }

        public override void OnDestroyView()
        {
            base.OnDestroyView();
            viewmodel.ViewModelParts.CollectionChanged -= HandleCollectionChanged;
        }

        void HandleCollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            list.GetAdapter().NotifyDataSetChanged();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            list = new RecyclerView(Activity);
            list.SetLayoutManager(new LinearLayoutManager(Activity));
            return list;
        }

        public static PostFragment NewFragment(int postId)
        {
            return NewFragment<PostFragment>(postId);
        }

        class Adapter : RecyclerView.Adapter
        {
            public PostViewModel viewmodel;
            int leftPaddingForChild;

            public override int GetItemViewType(int position)
            {
                var s = viewmodel.ViewModelParts[position];
                return s is Post ? 0 : 1;
            }

            public override void OnBindViewHolder(RecyclerView.ViewHolder holder, int position)
            {
                var h = (Holder)holder;

                leftPaddingForChild = (int)(holder.ItemView.Context.Resources.DisplayMetrics.Density * 20);
                if (holder.ItemViewType == 0)
                {
                    var item = (Post)viewmodel.ViewModelParts[position];
                    var webImage = (WebImageView)((ViewGroup)holder.ItemView).GetChildAt(0);
                    webImage.ImageSource = item.Image;
                }
                else if (holder.ItemViewType == 1)
                {
                    var item = (PostViewModel.CommentViewModel)viewmodel.ViewModelParts[position];
                    var button = holder.ItemView.FindViewById<TextView>(Resource.Id.title);

//                    button.Text = string.Format("({0}) {1}", item.ChildCount, item.Text);
                    button.Text = item.Text;
                    button.Visibility = string.IsNullOrEmpty(item.Text) ? ViewStates.Gone : ViewStates.Visible;

                    holder.ItemView.SetClick((sender, e) => item.NavigateCommand.Execute(null));

                    var avatar = holder.ItemView.FindViewById<WebImageView>(Resource.Id.icon);
                    avatar.ImageSource = item.UserImage;

                    var attach = holder.ItemView.FindViewById<WebImageView>(Resource.Id.attachment);
                    attach.ImageSizeDip = 80;
                    attach.ImageSource = item.Attachments.FirstOrDefault();
                    attach.Visibility = item.Attachments.Count > 0 ? ViewStates.Visible : ViewStates.Gone;

                    h.rating.Text = "" + item.Rating;
                    h.replies.Text = "" + item.ChildCount;
                    h.divider.Visibility = item.IsReply ? ViewStates.Visible : ViewStates.Gone;
                }
            }

            public override RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
            {
                if (viewType == 0)
                {
                    var panel = new FixedAspectPanel(parent.Context, null);
                    var webImage = new WebImageView(parent.Context, null);
                    panel.Aspect = 1; // FIXME:
                    panel.AddView(webImage);
                    return new Holder(panel);
                }
                else
                {
                    var view = View.Inflate(parent.Context, Resource.Layout.item_comment, null);
                    return Holder.New(view);
                }
            }

            public override int ItemCount
            {
                get { return viewmodel.ViewModelParts.Count; }
            }

            class Holder : RecyclerView.ViewHolder
            {
                internal TextView rating;
                internal TextView replies;
                internal View divider;

                public Holder(View view) : base(view)
                {
                    view.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent);
                }

                internal static Holder New(View view)
                {
                    return new Holder(view) {
                        rating = view.FindViewById<TextView>(Resource.Id.rating),
                        replies = view.FindViewById<TextView>(Resource.Id.replies),
                        divider = view.FindViewById(Resource.Id.divider),
                    };
                }
            }
        }
    }
}