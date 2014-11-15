using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;
using System.CodeDom.Compiler;

namespace JoyReactor.Ios
{
	partial class PostItemView : UICollectionViewCell
	{
		public PostItemView (IntPtr handle) : base (handle)
		{
		}

		public string Label {
			get { return label1.Text; }
			set { label1.Text = value; }
		}
	}
}
