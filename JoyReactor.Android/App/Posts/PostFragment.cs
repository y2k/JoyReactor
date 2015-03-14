using System.Collections.Specialized;
using Android.OS;
using System.Linq;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;
using JoyReactor.Core.Model.DTO;

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
                    button.Text = item.Text;
                    holder.ItemView.SetClick((sender, e) => item.NavigateCommand.Execute(null));
                    holder.ItemView.SetPadding(
                        item.IsRoot ? 0 : leftPaddingForChild, 
                        holder.ItemView.PaddingTop, 
                        holder.ItemView.PaddingRight, 
                        holder.ItemView.PaddingBottom);

                    var avatar = holder.ItemView.FindViewById<WebImageView>(Resource.Id.icon);
                    avatar.ImageSource = item.UserImage;

                    var attach = holder.ItemView.FindViewById<WebImageView>(Resource.Id.attachment);
                    attach.ImageSizeDip = 80;
                    attach.ImageSource = item.Attachments.FirstOrDefault();
                    attach.Visibility = item.Attachments.Count > 0 ? ViewStates.Visible : ViewStates.Gone;
                }
            }

            public override RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view;
                if (viewType == 0)
                {
                    var webImage = new WebImageView(parent.Context, null);
                    var panel = new FixedAspectPanel(parent.Context, null);
                    panel.Aspect = 1; // FIXME:
                    panel.AddView(webImage);
                    view = panel;
                }
                else
                {
                    view = View.Inflate(parent.Context, Resource.Layout.item_comment, null);
                }
                view.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent);
                return new Holder(view);
            }

            public override int ItemCount
            {
                get { return viewmodel.ViewModelParts.Count; }
            }

            class Holder : RecyclerView.ViewHolder
            {
                public Holder(View view)
                    : base(view)
                {
                }
            }
        }
    }
}