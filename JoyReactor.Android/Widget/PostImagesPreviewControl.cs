using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.Database;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;

namespace JoyReactor.Android.Widget
{
	public class PostImagesPreviewControl : FrameLayout
	{
		private GridView grid;

		public IListAdapter Adapter {
			get { return grid.Adapter; }
			set { grid.Adapter = value; }
		}

		public PostImagesPreviewControl (Context context, IAttributeSet attrs) :
			base (context, attrs)
		{
//			SetMinimumHeight ((int)(100 * Resources.DisplayMetrics.Density));

			grid = new GridView (Context);
			grid.StretchMode = StretchMode.StretchSpacing;
			grid.VerticalScrollBarEnabled = false;
			grid.SetColumnWidth((int)(100 * Resources.DisplayMetrics.Density));
			AddView (grid, new LayoutParams (LayoutParams.MatchParent, (int)(100 * Resources.DisplayMetrics.Density)));
		}

//		protected override void OnMeasure (int widthMeasureSpec, int heightMeasureSpec)
//		{
//			base.OnMeasure (widthMeasureSpec, heightMeasureSpec);
//		}
//
//		protected override void OnLayout (bool changed, int left, int top, int right, int bottom)
//		{
//			base.OnLayout (changed, left, top, right, bottom);
//		}
	}
}