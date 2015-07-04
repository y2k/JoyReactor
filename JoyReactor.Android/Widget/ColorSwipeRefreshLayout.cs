using Android.Content;
using Android.Support.V4.Widget;
using Android.Util;

namespace JoyReactor.Android.Widget
{
    public class ColorSwipeRefreshLayout : SwipeRefreshLayout
    {
        bool initialized;

        public ColorSwipeRefreshLayout(Context context, IAttributeSet attrs)
            : base(context, attrs)
        {
            SetColorScheme(
                global::Android.Resource.Color.HoloBlueBright, 
                global::Android.Resource.Color.HoloGreenLight,
                global::Android.Resource.Color.HoloOrangeLight, 
                global::Android.Resource.Color.HoloRedLight);
        }

        protected override void OnLayout(bool changed, int left, int top, int right, int bottom)
        {
            base.OnLayout(changed, left, top, right, bottom);
            FixFirstShow();
        }

        void FixFirstShow()
        {
            if (!initialized)
            {
                initialized = true;
                Refreshing = !Refreshing;
                Refreshing = !Refreshing;
            }
        }
    }
}