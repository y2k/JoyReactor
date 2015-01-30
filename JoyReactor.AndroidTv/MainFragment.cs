using System.Collections.Generic;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using JoyReactor.Core.ViewModels;
using JoyReactor.Core;

namespace JoyReactor.AndroidTv
{
    public class MainFragment : BrowseFragment
    {
        TagsViewModel tagsViewModel;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            RetainInstance = true;

            tagsViewModel = new TagsViewModel();
            Adapter = new ArrayObjectAdapterImpl { Items = tagsViewModel.Tags };

            tagsViewModel.Tags.CollectionChanged += (sender, e) => ((ArrayObjectAdapterImpl)Adapter).NotifyDataChanged();
        }

        public override void OnDestroy()
        {
            base.OnDestroy();
            tagsViewModel.Dispose();
        }

        class ArrayObjectAdapterImpl : ObjectAdapter
        {

            internal IList<TagsViewModel.TagItemViewModel> Items { get; set; }

            Dictionary<string, ListRow> cache = new Dictionary<string, ListRow>();

            internal ArrayObjectAdapterImpl()
                : base(new ListRowPresenter())
            {
            }

            public void NotifyDataChanged()
            {
                NotifyChanged();
            }

            public override Java.Lang.Object Get(int position)
            {
                var i = Items[position];
                if (!cache.ContainsKey(i.TagId.SerializeToString()))
                    cache[i.TagId.SerializeToString()] = new PostViewModelHolder(i).CreateRow();
                return cache[i.TagId.SerializeToString()];
            }

            public override int Size()
            {
                return Items.Count;
            }
        }
    }
}