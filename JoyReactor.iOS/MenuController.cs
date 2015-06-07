using System;
using Foundation;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using UIKit;

namespace JoyReactor.iOS
{
    public partial class MenuController : UIViewController
    {
        public MenuController(IntPtr handle)
            : base(handle)
        {
        }

        public override void ViewDidLoad()
        {
            base.ViewDidLoad();

            var viewmodel = new TagsViewModel();

            TagList.DataSource = new TagDataSource(viewmodel);
            TagList.Delegate = new Delegate(viewmodel);
            viewmodel.Tags.CollectionChanged += (sender, e) => TagList.ReloadData();
        }

        public class TagDataSource : UITableViewDataSource
        {
            TagsViewModel viewmodel;

            public TagDataSource(TagsViewModel viewmodel)
            {
                this.viewmodel = viewmodel;
            }

            public override UITableViewCell GetCell(UITableView tableView, NSIndexPath indexPath)
            {
                var view = tableView.DequeueReusableCell("TagCell");
                var item = viewmodel.Tags[(int)indexPath.Item];

                var imageView = (UIImageView)view.ViewWithTag(1);
                new ImageRequest()
                    .SetUrl(item.Image)
                    .Into<UIImage>(image => imageView.Image = image);
                ((UILabel)view.ViewWithTag(2)).Text = item.Title;

                imageView.Layer.CornerRadius = imageView.Bounds.Width / 2;
                imageView.Layer.MasksToBounds = true;

                return view;
            }

            public override nint RowsInSection(UITableView tableView, nint section)
            {
                return viewmodel.Tags.Count;
            }
        }

        class Delegate : UITableViewDelegate
        {
            TagsViewModel viewmodel;

            public Delegate(TagsViewModel viewmodel)
            {
                this.viewmodel = viewmodel;
            }

            public override void RowSelected(UITableView tableView, NSIndexPath indexPath)
            {
                viewmodel.SelectedTag = (int)indexPath.Item;
            }
        }
    }
}