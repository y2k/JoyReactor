using System;
using JoyReactor.Core.Model.Image;
using System.IO;
using System.Windows.Controls;
using System.Windows.Media.Imaging;

namespace JoyReactor.WP.Model
{
	public class BitmapImageDecoder : IImageDecoder
	{
		#region ImageDecoder implementation

		public object Decode (Stream stream)
		{
            var image = new BitmapImage();
            image.SetSource(stream);
            return image;
		}

		public int GetImageSize (ImageWrapper commonImage)
		{
            var image = (BitmapImage)commonImage.Image;
            return image.PixelWidth * image.PixelHeight * 4;
		}

		#endregion
	}
}