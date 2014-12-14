using System;
using MonoTouch.UIKit;
using JoyReactor.Core.ViewModels;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Ios
{
	partial class PostItemView : UICollectionViewCell
	{
		ImageModel model = ServiceLocator.Current.GetInstance<ImageModel> ();

		public PostItemView (IntPtr handle) : base (handle)
		{
		}

		public string Label {
			get { return label1.Text; }
			set { label1.Text = value; }
		}

		public void Update (FeedViewModel.ContentViewModel data)
		{
			label1.Text = data.Title;
			model.Load (Image,
				data.Image == null ? null : new Uri (data.Image), 
				100, 
				s => Image.Image = s as UIImage);
		}
	}
}