using Android.Content;
using Android.Support.V4.Widget;
using Android.Util;

namespace JoyReactor.Android.Widget
{
	public class ColorSwipeRefreshLayout : SwipeRefreshLayout
	{
		public ColorSwipeRefreshLayout (Context context, IAttributeSet attrs)
			: base (context, attrs)
		{
			SetColorScheme (
				global::Android.Resource.Color.HoloBlueBright, 
				global::Android.Resource.Color.HoloGreenLight,
				global::Android.Resource.Color.HoloOrangeLight, 
				global::Android.Resource.Color.HoloRedLight);
		}
	}
}