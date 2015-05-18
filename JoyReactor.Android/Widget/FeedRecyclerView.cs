using Android.Support.V7.Widget;
using Android.Content;
using Android.Util;

namespace JoyReactor.Android.Widget
{
    public class FeedRecyclerView : RecyclerView
    {
        public FeedRecyclerView(Context context, IAttributeSet attrs)
            : base(context, attrs)
        {
            SetLayoutManager (new StaggeredGridLayoutManager (2, StaggeredGridLayoutManager.Vertical));
            AddItemDecoration (new DividerItemDecoration (2.5f));
        }
    }
}