using Android.Content;
using Android.Util;
using Android.Views;
using Android.Widget;
using System;
using System.Windows.Input;

namespace JoyReactor.Android.Widget
{
	public class ReloadButton : FrameLayout
	{
		public ICommand Command { get; set; }

		public ReloadButton (Context context, IAttributeSet attrs) : base (context, attrs)
		{
			View.Inflate(context, Resource.Layout.layout_reload_button, this);
			FindViewById (Resource.Id.innerReloadButton).Click += (sender, e) => Command?.Execute(null);
		}
	}
}