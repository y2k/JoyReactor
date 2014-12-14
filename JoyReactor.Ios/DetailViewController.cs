using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using JoyReactor.Core.ViewModels;
using JoyReactor.Core;
using System.Collections.Generic;
using GalaSoft.MvvmLight;
using System.Collections.Specialized;

namespace JoyReactor.Ios
{
	public partial class DetailViewController : UICollectionViewController
	{
		UIPopoverController masterPopoverController;
		ID detailItem;
		FeedViewModel feedViewModel = new FeedViewModel ();

		public DetailViewController (IntPtr handle) : base (handle)
		{
		}

		public void SetDetailItem (ID newDetailItem)
		{
			if (detailItem != newDetailItem) {
				detailItem = newDetailItem;
				feedViewModel.ChangeCurrentListIdCommand.Execute (newDetailItem);
				
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

			CollectionView.DataSource = new PostSource { Items = feedViewModel.Posts };
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();
			
			// Perform any additional setup after loading the view, typically from a nib.
			ConfigureView ();
		}

		public override void ViewWillAppear (bool animated)
		{
			base.ViewWillAppear (animated);
			feedViewModel.Posts.CollectionChanged += HandleCollectionChanged;
		}

		public override void ViewDidDisappear (bool animated)
		{
			base.ViewDidDisappear (animated);
			feedViewModel.Posts.CollectionChanged -= HandleCollectionChanged;
		}

		void HandleCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			CollectionView.ReloadData ();
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
			public IList<ViewModelBase> Items { get; set; }

			#region implemented abstract members of UICollectionViewDataSource

			public override UICollectionViewCell GetCell (UICollectionView collectionView, NSIndexPath indexPath)
			{
				var cell = (PostItemView)collectionView.DequeueReusableCell (new NSString ("cell"), indexPath);

				var s = Items [indexPath.Row] as FeedViewModel.ContentViewModel;
				if (s != null)
					cell.Update (s);

				return cell; 
			}

			public override int GetItemsCount (UICollectionView collectionView, int section)
			{
				return Items.Count;
			}

			#endregion
		}
	}
}