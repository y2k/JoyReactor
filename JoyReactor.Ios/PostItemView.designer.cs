// WARNING
//
// This file has been generated automatically by Xamarin Studio from the outlets and
// actions declared in your storyboard file.
// Manual changes to this file will not be maintained.
//
using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using System.CodeDom.Compiler;

namespace JoyReactor.Ios
{
	[Register ("PostItemView")]
	partial class PostItemView
	{
		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UILabel label1 { get; set; }

		void ReleaseDesignerOutlets ()
		{
			if (label1 != null) {
				label1.Dispose ();
				label1 = null;
			}
		}
	}
}
