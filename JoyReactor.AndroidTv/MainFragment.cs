using System.Collections.Generic;
using Android.Content;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using JoyReactor.AndroidTv.Helpers;
using JoyReactor.Core.ViewModels;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;
using System;

namespace JoyReactor.AndroidTv
{
    public class MainFragment : BrowseFragment
    {
        TagsViewModel tagsViewModel;
        BackgroundChanger backgroundMonitor;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            tagsViewModel = new TagsViewModel();
            backgroundMonitor = new BackgroundChanger(Activity);

            Adapter = new ArrayObjectAdapterImpl { Items = tagsViewModel.Tags };

            tagsViewModel.Tags.CollectionChanged += (sender, e) => ((ArrayObjectAdapterImpl)Adapter).NotifyDataChanged();

            OnActivityCreated(savedInstanceState);
            ItemViewClicked += (sender, e) =>
            {
                var item = ((PostPresenter.PostWrapper)e.Item).Post as FeedViewModel.ContentViewModel;
                if (item != null)
                    item.OpenPostCommand.Execute(null);
            };
            ItemSelected += (sender, e) => backgroundMonitor.Change(new Random().Next(0xFFFFFF));

            Messenger.Default.Register<PostNavigationMessage>(this, msg =>
                {
                    Intent intent = new Intent(Activity, typeof(PostActivity));
                    intent.PutExtra(PostActivity.PostId, msg.PostId);
                    Activity.StartActivity(intent);
                });

            Title = "JoyReactor (github.com/y2k)";
        }

        public override void OnDestroy()
        {
            base.OnDestroy();
            tagsViewModel.Cleanup();
            backgroundMonitor.Dispose();
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
                if (position < Items.Count)
                {
                    var i = Items[position];
                    if (!cache.ContainsKey(i.TagId.SerializeToString()))
                        cache[i.TagId.SerializeToString()] = new PostViewModelHolder(i).CreateRow();
                    return cache[i.TagId.SerializeToString()];
                }
                else
                {
                    GridItemPresenter mGridPresenter = new GridItemPresenter();
                    ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
                    gridRowAdapter.Add(new Java.Lang.String("Profile"));
                    gridRowAdapter.Add(new Java.Lang.String("Private messages"));
                    return new ListRow(new HeaderItem("PREFERENCES", null), gridRowAdapter);
                }
            }

            public override int Size()
            {
                return Items.Count + 1;
            }
        }
    }
}