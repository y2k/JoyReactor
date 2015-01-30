using System.Collections.Specialized;
using System.Linq;
using Android.Support.V17.Leanback.Widget;
using GalaSoft.MvvmLight;
using JoyReactor.Core.ViewModels;
using System.Collections;
using System.Collections.Generic;

namespace JoyReactor.AndroidTv
{
    class PostViewModelHolder
    {
        readonly ArrayObjectAdapterImpl listRowAdapter;

        public PostViewModelHolder(TagsViewModel.TagItemViewModel tag, ArrayObjectAdapter adapter)
        {
            var feedViewModel = new FeedViewModel();
            feedViewModel.Posts.CollectionChanged += (sender, e) => listRowAdapter.NotifyDataChanged();
            listRowAdapter = new ArrayObjectAdapterImpl { Items = feedViewModel.Posts };

            feedViewModel.Initialize(tag.TagId);

            adapter.Add(new ListRow(new HeaderItem(tag.Title, null), listRowAdapter));
        }

        class ArrayObjectAdapterImpl : ObjectAdapter {

            internal IList<ViewModelBase> Items { get; set; }

            internal ArrayObjectAdapterImpl() : base(new PostPresenter()) { }

            public void NotifyDataChanged() {
                NotifyChanged();
            }

            #region implemented abstract members of ObjectAdapter

            public override Java.Lang.Object Get(int position)
            {
                return new PostPresenter.PostWrapper { Post = Items[position] };
            }

            public override int Size()
            {
                return Items.Count;
            }

            #endregion
        }
    }
}