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
	[Register ("HomeController")]
	partial class HomeController
	{
		[Outlet]
		UIKit.UIButton ApplyNewPosts { get; set; }

		[Outlet]
		UIKit.UICollectionView PostList { get; set; }

		[Outlet]
		UIKit.UIView SideMenu { get; set; }
		
		void ReleaseDesignerOutlets ()
		{
			if (PostList != null) {
				PostList.Dispose ();
				PostList = null;
			}

			if (SideMenu != null) {
				SideMenu.Dispose ();
				SideMenu = null;
			}

			if (ApplyNewPosts != null) {
				ApplyNewPosts.Dispose ();
				ApplyNewPosts = null;
			}
		}
	}
}
