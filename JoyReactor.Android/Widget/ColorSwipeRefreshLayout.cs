using Android.Content;
using Android.Support.V4.Widget;
using Android.Util;
using Com.Android.EX.Widget;
using Java.Interop;

namespace JoyReactor.Android.Widget
{
	public class ColorSwipeRefreshLayout : SwipeRefreshLayout
	{
		public ColorSwipeRefreshLayout (Context context, IAttributeSet attrs)
			: base (context, attrs)
		{
		}

		protected override void OnAttachedToWindow ()
		{
			base.OnAttachedToWindow ();
			SetColorScheme (
				global::Android.Resource.Color.HoloBlueBright, 
				global::Android.Resource.Color.HoloGreenLight,
				global::Android.Resource.Color.HoloOrangeLight, 
				global::Android.Resource.Color.HoloRedLight);
		}

		public override bool CanChildScrollUp ()
		{
			StaggeredGridView s = null;
			try {
				s = GetChildAt (0).JavaCast<StaggeredGridView> ();
			} catch {
			}

			if (s != null) {
				return s.ChildCount > 0 && (s.FirstPosition > 0 || s.GetChildAt (0).Top < s.PaddingTop);
			} else {
				return base.CanChildScrollUp ();
			}
		}
	}
}