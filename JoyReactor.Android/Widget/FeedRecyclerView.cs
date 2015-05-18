using Android.Support.V7.Widget;
using Android.Content;
using Android.Util;
using Android.Widget;

namespace JoyReactor.Android.Widget
{
    public class FeedRecyclerView : FrameLayout
    {
        RecyclerView list;

        public FeedRecyclerView(Context context, IAttributeSet attrs)
            : base(context, attrs)
        {
            Initialize();
        }

        public override bool CanScrollVertically(int direction)
        {
            return list.CanScrollVertically(direction);
        }

        void Initialize()
        {
            RemoveAllViews();

            list = new RecyclerView(Context);
            list.SetLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.Vertical));
            list.AddItemDecoration(new DividerItemDecoration(3f));
            var padding = (int)(3 * Resources.DisplayMetrics.Density);
            list.SetPadding(padding, padding, padding, padding);
            list.SetClipToPadding(false);

            AddView(list);
        }

        public void ResetScrollToTop()
        {
            var adapter = list.GetAdapter();
            Initialize();
            list.SetAdapter(adapter);
        }

        public void SetAdapter(RecyclerView.Adapter adapter)
        {
            list.SetAdapter(adapter);
        }
    }
}