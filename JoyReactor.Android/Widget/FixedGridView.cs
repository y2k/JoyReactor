
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Util;
using Android.Views;
using Android.Widget;
using Android.Database;

namespace JoyReactor.Android.Widget
{
	public class FixedGridView : ViewGroup
	{
		private const float MinItemWidth = 100;

		private Observer observer;
		private IListAdapter _adapter;
		private bool isDataChanged;

		private int divider;
		private int itemWidth;

		public IListAdapter Adapter {
			get { return _adapter; }
			set { SetAdapter (value); }
		}

		public FixedGridView (Context context) :
			base (context)
		{
			Initialize ();
		}

		public FixedGridView (Context context, IAttributeSet attrs) :
			base (context, attrs)
		{
			Initialize ();
		}

		public FixedGridView (Context context, IAttributeSet attrs, int defStyle) :
			base (context, attrs, defStyle)
		{
			Initialize ();
		}

		void Initialize ()
		{
			observer = new Observer { parent = this };
			divider = (int)(2 * Resources.DisplayMetrics.Density);
			SetPadding (divider, divider, divider, divider);
			SetClipToPadding (false);
		}

		#region implemented abstract members of ViewGroup

		protected override void OnMeasure (int widthMeasureSpec, int heightMeasureSpec)
		{
			base.OnMeasure (widthMeasureSpec, heightMeasureSpec);

			int w = MeasureSpec.GetSize (widthMeasureSpec) - PaddingLeft - PaddingRight;
			int c = Math.Max (1, (int)((w + divider) / (MinItemWidth * Resources.DisplayMetrics.Density + divider)));
			itemWidth = (w - (c - 1) * divider) / c;
			SetMeasuredDimension (MeasuredWidth, itemWidth + PaddingTop + PaddingBottom);

			if (isDataChanged) {
				isDataChanged = false;

				RemoveAllViewsInLayout ();
				for (int i = 0; i < _adapter.Count; i++) AddView (_adapter.GetView (i, null, this));
			}

			var m = MeasureSpec.MakeMeasureSpec (itemWidth, MeasureSpecMode.Exactly);
			for (int i = 0; i < ChildCount; i++) GetChildAt (i).Measure (m, m);
		}

		protected override void OnLayout (bool changed, int l, int t, int r, int b)
		{
			for (int i = 0; i < ChildCount; i++) {
				var x = PaddingLeft + i * (itemWidth + divider);
				GetChildAt (i).Layout (x, PaddingTop, x + itemWidth, PaddingTop + itemWidth);
			}
		}

		#endregion

		private void SetAdapter(IListAdapter adapter) {
			if (_adapter != adapter) {
				if (_adapter != null) _adapter.UnregisterDataSetObserver (observer);
				_adapter = adapter;
				if (_adapter != null) _adapter.RegisterDataSetObserver(observer);
				isDataChanged = true;
				RequestLayout ();
			}
		}

		private class Observer : DataSetObserver {

			internal FixedGridView parent;

			public override void OnChanged ()
			{
				parent.isDataChanged = true;
				parent.RequestLayout ();
			}

			public override void OnInvalidated ()
			{
				OnChanged ();
			}
		}
	}
}