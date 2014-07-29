
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
using Android.Graphics;

namespace JoyReactor.Android.Widget
{
	public class PostImageLayout : FrameLayout
	{
		public PostImageLayout (Context context) :
			base (context)
		{
			Initialize ();
		}

		public PostImageLayout (Context context, IAttributeSet attrs) :
			base (context, attrs)
		{
			Initialize ();
		}

		public PostImageLayout (Context context, IAttributeSet attrs, int defStyle) :
			base (context, attrs, defStyle)
		{
			Initialize ();
		}

		void Initialize ()
		{
		}

		protected override void OnAttachedToWindow ()
		{
			base.OnAttachedToWindow ();
			ViewTreeObserver.ScrollChanged += HandleScrollChanged;
		}

		void HandleScrollChanged (object sender, EventArgs e)
		{
			ScrollY = Top / 2;
		}

		protected override void OnDetachedFromWindow ()
		{
			base.OnDetachedFromWindow ();
			ViewTreeObserver.ScrollChanged -= HandleScrollChanged;
		}
	}
}