using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;
using JoyReactor.Android.App.Base;
using JoyReactor.Core.ViewModels;
using Android.Support.V7.Widget;
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
		}

		public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			return list = new RecyclerView (Activity);
		}

		public static OldPostFragment NewFragment (int postId)
		{
			return NewFragment<OldPostFragment> (postId);
		}

		class Adapter : RecyclerView.Adapter
		{
			public PostViewModel viewmodel;

			public override int GetItemViewType (int position)
			{
				return position == 0 ? 0 : 1;
			}

			public override void OnBindViewHolder (RecyclerView.ViewHolder holder, int position)
			{
				throw new NotImplementedException ();
			}

			public override RecyclerView.ViewHolder OnCreateViewHolder (ViewGroup parent, int viewType)
			{
				if (viewType == 0) {
					return new Holder (new WebImageView (parent.Context, null));
				}
				return new Holder (new Button (parent.Context));
			}

			public override int ItemCount {
				get { return 1 + viewmodel.Comments.Count; }
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