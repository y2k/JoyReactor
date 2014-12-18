using System;
using System.Collections.Generic;
using Android.Views;

namespace JoyReactor.Android.App.Base
{
	public static class ViewExtensions
	{
		static readonly List<ClickRecord> records = new List<ClickRecord>();

		public static void SetClick(this View view, EventHandler listener) {
			for (int i = records.Count - 1; i >= 0; i--) {
				var s = records [i];
				View v;
				if (s.view.TryGetTarget (out v)) {
					if (v == view) {
						records.RemoveAt (i);
						view.Click -= s.handler;
						break;
					}
				} else
					records.RemoveAt (i);
			}

			view.Click += listener;
			records.Add (new ClickRecord { view = new WeakReference<View> (view), handler = listener });
		}

		struct ClickRecord {
			public WeakReference<View> view;
			public EventHandler handler;
		}
	}
}