using System;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Drawing;
using JoyReactor.Core.ViewModels;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

namespace JoyReactor.Ios
{
	public partial class MasterViewController : UITableViewController
	{
		DataSource dataSource;

		TagsViewModel tags = new TagsViewModel ();

		public MasterViewController (IntPtr handle) : base (handle)
		{
			Title = NSBundle.MainBundle.LocalizedString ("Master", "Master");

			if (UIDevice.CurrentDevice.UserInterfaceIdiom == UIUserInterfaceIdiom.Pad) {
				ContentSizeForViewInPopover = new SizeF (320f, 600f);
				ClearsSelectionOnViewWillAppear = false;
			}
		}

		public DetailViewController DetailViewController {
			get;
			set;
		}

		public async override void ViewDidLoad ()
		{
			base.ViewDidLoad ();
			TableView.Source = dataSource = new DataSource (this);
		}

		public override void ViewWillAppear (bool animated)
		{
			base.ViewWillAppear (animated);
			tags.Tags.CollectionChanged += HandleTagCollectionChanged;
		}

		public override void ViewDidDisappear (bool animated)
		{
			base.ViewDidDisappear (animated);
			tags.Tags.CollectionChanged -= HandleTagCollectionChanged;
		}

		void HandleTagCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			TableView.ReloadData ();
		}

		void HandlePostsCollectionChanged (object sender, NotifyCollectionChangedEventArgs e)
		{
			// TODO
			TableView.ReloadData ();
		}

		class DataSource : UITableViewSource
		{
			static readonly NSString CellIdentifier = new NSString ("Cell");
			readonly MasterViewController controller;
			ObservableCollection<TagsViewModel.TagItemViewModel> objects;

			public DataSource (MasterViewController controller)
			{
				this.controller = controller;
				objects = controller.tags.Tags;
			}

			public ObservableCollection<TagsViewModel.TagItemViewModel> Objects {
				get { return objects; }
			}

			// Customize the number of sections in the table view.
			public override int NumberOfSections (UITableView tableView)
			{
				return 1;
			}

			public override int RowsInSection (UITableView tableview, int section)
			{
				return controller.tags.Tags.Count;
			}

			// Customize the appearance of table view cells.
			public override UITableViewCell GetCell (UITableView tableView, NSIndexPath indexPath)
			{
				var cell = tableView.DequeueReusableCell (CellIdentifier, indexPath);
				cell.TextLabel.Text = objects [indexPath.Row].Title;
				return cell;
			}

			public override void RowSelected (UITableView tableView, NSIndexPath indexPath)
			{
				if (UIDevice.CurrentDevice.UserInterfaceIdiom == UIUserInterfaceIdiom.Pad)
					controller.DetailViewController.SetDetailItem (objects [indexPath.Row].TagId);
			}
		}

		public override void PrepareForSegue (UIStoryboardSegue segue, NSObject sender)
		{
			if (segue.Identifier == "showDetail") {
				var indexPath = TableView.IndexPathForSelectedRow;
				var item = dataSource.Objects [indexPath.Row];

				((DetailViewController)segue.DestinationViewController).SetDetailItem (item.TagId);
			}
		}
	}
}