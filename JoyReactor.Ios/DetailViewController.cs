using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Ios
{
	public partial class DetailViewController : UICollectionViewController
	{
		UIPopoverController masterPopoverController;
		Tag detailItem;

		public DetailViewController (IntPtr handle) : base (handle)
		{
		}

		public void SetDetailItem (Tag newDetailItem)
		{
			if (detailItem != newDetailItem) {
				detailItem = newDetailItem;
				
				// Update the view
				ConfigureView ();
			}
			
			if (masterPopoverController != null)
				masterPopoverController.Dismiss (true);
		}

		void ConfigureView ()
		{
			// Update the user interface for the detail item
//			if (IsViewLoaded && detailItem != null)
//				detailDescriptionLabel.Text = detailItem.ToString ();

			CollectionView.DataSource = new PostSource ();
		}

		public override void DidReceiveMemoryWarning ()
		{
			// Releases the view if it doesn't have a superview.
			base.DidReceiveMemoryWarning ();

			// Release any cached data, images, etc that aren't in use.
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();
			
			// Perform any additional setup after loading the view, typically from a nib.
			ConfigureView ();
		}

		[Export ("splitViewController:willHideViewController:withBarButtonItem:forPopoverController:")]
		public void WillHideViewController (UISplitViewController splitController, UIViewController viewController, UIBarButtonItem barButtonItem, UIPopoverController popoverController)
		{
			barButtonItem.Title = NSBundle.MainBundle.LocalizedString ("Master", "Master");
			NavigationItem.SetLeftBarButtonItem (barButtonItem, true);
			masterPopoverController = popoverController;
		}

		[Export ("splitViewController:willShowViewController:invalidatingBarButtonItem:")]
		public void WillShowViewController (UISplitViewController svc, UIViewController vc, UIBarButtonItem button)
		{
			// Called when the view is shown again in the split view, invalidating the button and popover controller.
			NavigationItem.SetLeftBarButtonItem (null, true);
			masterPopoverController = null;
		}

		class PostSource : UICollectionViewDataSource
		{
			Random rand = new Random ();

			#region implemented abstract members of UICollectionViewDataSource

			public override UICollectionViewCell GetCell (UICollectionView collectionView, NSIndexPath indexPath)
			{
				var cell = (PostItemView)collectionView.DequeueReusableCell (new NSString ("cell"), indexPath);
				cell.Label = "" + rand.Next ();
				return cell; 

			}

			public override int GetItemsCount (UICollectionView collectionView, int section)
			{
				return 10;
			}

			#endregion
		}
	}
}