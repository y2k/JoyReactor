using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using Android.Graphics;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.Widget
{
	public class WebImageView : ImageView
	{
		private IImageModel iModel = ServiceLocator.Current.GetInstance<IImageModel> ();

		private string imageSource;

		public string ImageSource {
			get { return imageSource; }
			set { UpdateImageSource (value); }
		}

		public WebImageView (Context context, global::Android.Util.IAttributeSet attrs) : base (context, attrs) {
//			Initialize ();
		}

		private void Initialize () {
			throw new NotImplementedException ();
		}

		private void UpdateImageSource (string imageSource) {
			if (this.imageSource != imageSource) {
				this.imageSource = imageSource;

				var u = imageSource == null ? null : new Uri (imageSource); // u == null отменяет закачки
				iModel.Load (this, u, 0, s => {
					if (s.Image == null) SetImageDrawable(null);
					else SetImageBitmap ((Bitmap)s.Image);
				}); 
			}
		}

	}
}