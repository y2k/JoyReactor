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
using System.Drawing;

namespace JoyReactor.Android.Widget
{
	public class FixedSizeImageView : ImageView
	{
		public Size ImageSize { get; set; }

		public FixedSizeImageView (Context context) :
			base (context)
		{
			Initialize ();
		}

		public FixedSizeImageView (Context context, IAttributeSet attrs) :
			base (context, attrs)
		{
			Initialize ();
		}

		public FixedSizeImageView (Context context, IAttributeSet attrs, int defStyle) :
			base (context, attrs, defStyle)
		{
			Initialize ();
		}

		protected override void OnMeasure (int widthMeasureSpec, int heightMeasureSpec)
		{
			int w = MeasureSpec.GetSize(widthMeasureSpec);
			int iw = ImageSize.Width > 0 ? ImageSize.Width : 1;
			int ih = ImageSize.Height > 0 ? ImageSize.Height : 1;
			SetMeasuredDimension(w, (int) (((float) w / iw) * ih));
		}

		void Initialize ()
		{
			SetScaleType (ScaleType.CenterCrop);
		}
	}
}