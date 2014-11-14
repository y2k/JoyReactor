using System;
using Android.Content;
using Android.Graphics;
using Android.Widget;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Android.Widget
{
	public class WebImageView : ImageView
	{
		ImageModel iModel = ServiceLocator.Current.GetInstance<ImageModel> ();

		string imageSource;

		public string ImageSource {
			get { return imageSource; }
			set { UpdateImageSource (value); }
		}

		public WebImageView (Context context, global::Android.Util.IAttributeSet attrs) : base (context, attrs)
		{
//			Initialize ();
		}

		void Initialize ()
		{
			throw new NotImplementedException ();
		}

		void UpdateImageSource (string imageSource)
		{
			if (this.imageSource != imageSource) {
				this.imageSource = imageSource;

				var u = imageSource == null ? null : new Uri (imageSource); // u == null отменяет закачки
				iModel.Load (this, u, 0, s => {
					if (s == null)
						SetImageDrawable (null);
					else
						SetImageBitmap ((Bitmap)s);
				}); 
			}
		}
	}
}