using System;
using Foundation;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using UIKit;
using CoreGraphics;

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
            PostList.Delegate = new Delegate();
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
                try
                {
                    var item = (FeedViewModel.ContentViewModel)viewmodel.Posts[(int)indexPath.Item];
                    new ImageRequest()
                        .SetUrl(item.Image)
                        .CropIn(300)
                        .Into<UIImage>(image => ((UIImageView)view.ViewWithTag(1)).Image = image);
                    ((UILabel)view.ViewWithTag(2)).Text = item.UserName;
                }
                catch
                {
                }
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
            public override CGSize GetSizeForItem(UICollectionView collectionView, UICollectionViewLayout layout, NSIndexPath indexPath)
            {
                var size = (collectionView.Bounds.Width - 10) / 2;
                return new CGSize(size, 1.5f * size);
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