using System;
using CoreGraphics;
using Foundation;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
using UIKit;

namespace JoyReactor.iOS
{
    public partial class HomeController : UIViewController
    {
        public HomeController(IntPtr handle)
            : base(handle)
        {
        }

        public override void ViewDidLoad()
        {
            base.ViewDidLoad();

            var viewmodel = new FeedViewModel();
            PostList.DataSource = new DataSource(viewmodel);
            PostList.Delegate = new Delegate(viewmodel);
            viewmodel.Posts.CollectionChanged += (sender, e) => PostList.ReloadData();

            var button = new UIBarButtonItem { Title = "☰" };
            button.Clicked += (sender, e) => SideMenu.Hidden = !SideMenu.Hidden;
            NavigationItem.LeftBarButtonItem = button;
        }

        public class DataSource : UICollectionViewDataSource
        {
            FeedViewModel viewmodel;

            public DataSource(FeedViewModel viewmodel)
            {
                this.viewmodel = viewmodel;
            }

            public override UICollectionViewCell GetCell(UICollectionView collectionView, NSIndexPath indexPath)
            {
                UICollectionViewCell view;
                var item = viewmodel.Posts[(int)indexPath.Item];
                if (item is FeedViewModel.Divider)
                {
                    view = (UICollectionViewCell)collectionView.DequeueReusableCell("DividerCell", indexPath);
                }
                else
                {
                    view = (UICollectionViewCell)collectionView.DequeueReusableCell("PostCell", indexPath);
                    view.Layer.CornerRadius = 8;
                    new ImageRequest()
                        .SetUrl(item.Image)
                        .CropIn(300)
                        .Into<UIImage>(image => ((UIImageView)view.ViewWithTag(1)).Image = image);
                    var userImage = (UIImageView)view.ViewWithTag(3);
                    userImage.Layer.CornerRadius = userImage.Bounds.Width / 2;
                    new ImageRequest()
                        .SetUrl(item.UserImage)
                        .CropIn(40)
                        .Into<UIImage>(image => userImage.Image = image);
                    ((UILabel)view.ViewWithTag(2)).Text = item.UserName;
                }
                return view;
            }

            public override nint GetItemsCount(UICollectionView collectionView, nint section)
            {
                return viewmodel.Posts.Count;
            }
        }

        public class Delegate : UICollectionViewDelegateFlowLayout
        {
            FeedViewModel viewmodel;

            public Delegate(FeedViewModel viewmodel)
            {
                this.viewmodel = viewmodel;
            }

            public override CGSize GetSizeForItem(UICollectionView collectionView, UICollectionViewLayout layout, NSIndexPath indexPath)
            {
                const float space = 5f;
                var post = viewmodel.Posts[(int)indexPath.Item];
                if (post is FeedViewModel.Divider)
                    return new CGSize(collectionView.Bounds.Width - 2 * space, 50);

                int col = (int)((collectionView.Bounds.Width - space) / (200 + space));
                var width = (collectionView.Bounds.Width - space * (1 + col)) / col;
                var height = 66 + width * post.ImageHeight / post.ImageWidth;
                return new CGSize(width, height);
            }
        }
    }
}