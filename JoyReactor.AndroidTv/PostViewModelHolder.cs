using System.Collections.Generic;
using Android.Support.V17.Leanback.Widget;
using GalaSoft.MvvmLight;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.AndroidTv
{
    class PostViewModelHolder
    {
        ArrayObjectAdapterImpl listRowAdapter;
        TagsViewModel.TagItemViewModel tag;

        public PostViewModelHolder(TagsViewModel.TagItemViewModel tag)
        {
            this.tag = tag;
        }

        internal ListRow CreateRow() 
        {
            var feedViewModel = new FeedViewModel();
            feedViewModel.Posts.CollectionChanged += (sender, e) => listRowAdapter.NotifyDataChanged();
            listRowAdapter = new ArrayObjectAdapterImpl { Items = feedViewModel.Posts };
            feedViewModel.Initialize(tag.TagId);
            return new ListRow(new HeaderItem(tag.Title, null), listRowAdapter);
        }

        class ArrayObjectAdapterImpl : ObjectAdapter {

            internal IList<ViewModelBase> Items { get; set; }

            internal ArrayObjectAdapterImpl() : base(new PostPresenter()) { }

            public void NotifyDataChanged() {
                NotifyChanged();
            }

            public override Java.Lang.Object Get(int position)
            {
                return new PostPresenter.PostWrapper { Post = Items[position] };
            }

            public override int Size()
            {
                return Items.Count;
            }
        }
    }
}