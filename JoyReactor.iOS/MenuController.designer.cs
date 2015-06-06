// WARNING
//
// This file has been generated automatically by Xamarin Studio to store outlets and
// actions made in the UI designer. If it is removed, they will be lost.
// Manual changes to this file may not be handled correctly.
//
using Foundation;
using System.CodeDom.Compiler;

namespace JoyReactor.iOS
{
	[Register ("MenuController")]
	partial class MenuController
	{
		[Outlet]
		UIKit.UITableView TagList { get; set; }
		
		void ReleaseDesignerOutlets ()
		{
			if (TagList != null) {
				TagList.Dispose ();
				TagList = null;
			}
		}
	}
}
