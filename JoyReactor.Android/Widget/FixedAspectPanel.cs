
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Util;

namespace JoyReactor.Android.Widget
{
	public class FixedAspectPanel : ViewGroup
	{
		private float _aspect = 1;

		public float Aspect {
			get { return _aspect; }
			set {
				_aspect = value;
				RequestLayout ();
			}
		}

		public FixedAspectPanel (Context context, IAttributeSet attrs) : base (context, attrs)
		{
		}

		protected override void OnMeasure (int widthMeasureSpec, int heightMeasureSpec)
		{
			int w, h;
			if (MeasureSpec.GetSize (widthMeasureSpec) != 0) {
				w = MeasureSpec.GetSize (widthMeasureSpec);
				h = (int)(w / _aspect);
			} else if (MeasureSpec.GetSize (widthMeasureSpec) != 0) {
				h = MeasureSpec.GetSize (heightMeasureSpec);
				w = (int)(h * _aspect);
			} else throw new Exception ();
			SetMeasuredDimension (w, h);

			for (int i = 0; i < ChildCount; i++) {
				GetChildAt (i).Measure (
					MeasureSpec.MakeMeasureSpec (w, MeasureSpecMode.Exactly),
					MeasureSpec.MakeMeasureSpec (h, MeasureSpecMode.Exactly));
			}
		}

		#region implemented abstract members of ViewGroup

		protected override void OnLayout (bool changed, int l, int t, int r, int b)
		{
			for (int i = 0; i < ChildCount; i++) {
				GetChildAt (i).Layout (l, t, r, b);
			}
		}

		#endregion
	}
}