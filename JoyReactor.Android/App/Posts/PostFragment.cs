using System.Collections.Specialized;
using Android.OS;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using JoyReactor.Android.Widget;

namespace JoyReactor.Android.App.Posts
{
	public class PostFragment : BaseFragment
	{
		PostViewModel viewmodel = new PostViewModel ();
		RecyclerView list;

		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
			RetainInstance = true;
			viewmodel.Initialize (Arguments.GetInt (Arg1));
		}

		public override void OnActivityCreated (Bundle savedInstanceState)
		{
			base.OnActivityCreated (savedInstanceState);
			list.SetAdapter (new Adapter { viewmodel = viewmodel });
			viewmodel.ViewModelParts.CollectionChanged += HandleCollectionChanged;
		}

		public override void OnDestroyView ()
		{
			base.OnDestroyView ();
			viewmodel.ViewModelParts.CollectionChanged -= HandleCollectionChanged;
		}

		void HandleCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			list.GetAdapter ().NotifyDataSetChanged ();
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			list = new RecyclerView (Activity);
			list.SetLayoutManager (new LinearLayoutManager (Activity));
			return list;
		}

		public static PostFragment NewFragment (int postId)
		{
			return NewFragment<PostFragment> (postId);
		}

		class Adapter : RecyclerView.Adapter
		{
			public PostViewModel viewmodel;

			public override int GetItemViewType (int position)
			{
				var s = viewmodel.ViewModelParts [position];
				return s is PostViewModel.PosterViewModel ? 0 : 1;
			}

			public override void OnBindViewHolder (RecyclerView.ViewHolder holder, int position)
			{
				if (holder.ItemViewType == 0) {
					var item = (PostViewModel.PosterViewModel)viewmodel.ViewModelParts [position];
					var webImage = (WebImageView)((ViewGroup)holder.ItemView).GetChildAt (0);
					webImage.ImageSource = item.Image;
				} else if (holder.ItemViewType == 1) {
					var item = (PostViewModel.CommentViewModel)viewmodel.ViewModelParts [position];
					var button = holder.ItemView.FindViewById<TextView> (Resource.Id.title);
					button.Text = (item.IsRoot ? "" : ">> ") + item.Text;
					holder.ItemView.SetClick ((sender, e) => item.NavigateCommand.Execute (null));
				}
			}

			public override RecyclerView.ViewHolder OnCreateViewHolder (ViewGroup parent, int viewType)
			{
				View view;
				if (viewType == 0) {
					var webImage = new WebImageView (parent.Context, null);
					var panel = new FixedAspectPanel (parent.Context, null);
					panel.Aspect = 1; // FIXME:
					panel.AddView (webImage);
					view = panel;
				} else {
					view = View.Inflate (parent.Context, Resource.Layout.item_comment, null);
				}
				view.LayoutParameters = new StaggeredGridLayoutManager.LayoutParams (
					ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.WrapContent);
				return new Holder (view);
			}

			public override int ItemCount {
				get { return viewmodel.ViewModelParts.Count; }
			}

			class Holder : RecyclerView.ViewHolder
			{
				public Holder (View view) : base (view)
				{
				}
			}
		}
	}
}