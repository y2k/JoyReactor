using System;
using CoreGraphics;
using Foundation;
using JoyReactor.Core;
using JoyReactor.Core.Model;
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

            viewmodel.Initialize(ID.Factory.New(ID.IdConst.ReactorGood));
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
                var view = (UICollectionViewCell)collectionView.DequeueReusableCell("PostCell", indexPath);
                view.Layer.CornerRadius = 8;
                var item = (FeedViewModel.ContentViewModel)viewmodel.Posts[(int)indexPath.Item];
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
                return view;
            }

            public override nint GetItemsCount(UICollectionView collectionView, nint section)
            {
                return viewmodel.Posts.Count - 1;
            }

            public override nint NumberOfSections(UICollectionView collectionView)
            {
                return 1;
            }

            public override UICollectionReusableView GetViewForSupplementaryElement(UICollectionView collectionView, NSString elementKind, NSIndexPath indexPath)
            {
                return collectionView.DequeueReusableSupplementaryView(elementKind, "Divider", indexPath);
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
                var item = (FeedViewModel.ContentViewModel)viewmodel.Posts[(int)indexPath.Item];
                var width = collectionView.Bounds.Width - 10;
                var height = 66 + width * item.ImageHeight / item.ImageWidth;
                return new CGSize(width, height);
            }

            //            public override void ItemSelected(UICollectionView collectionView, NSIndexPath indexPath)
            //            {
            //                // TODO:
            //            }

            //            public override void ItemHighlighted(UICollectionView collectionView, NSIndexPath indexPath)
            //            {
            //                collectionView.CellForItem(indexPath).BackgroundColor = UIColor.FromRGBA(0xC0, 0xC0, 0xFF, 0x80);
            //            }
            //
            //            public override void ItemUnhighlighted(UICollectionView collectionView, NSIndexPath indexPath)
            //            {
            //                collectionView.CellForItem(indexPath).BackgroundColor = null;
            //            }
        }
    }
}