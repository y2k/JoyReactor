using System;
using JoyReactor.Core.Model.Image;
using System.IO;
using Android.Graphics;

namespace JoyReactor.Android.Model
{
	public class BitmapImageDecoder : IImageDecoder
	{
		#region ImageDecoder implementation

		public object Decode (Stream stream)
		{
			return BitmapFactory.DecodeStream (stream);
		}

		public int GetImageSize (ImageWrapper commonImage)
		{
			return ((Bitmap)commonImage.Image).ByteCount;
		}

		#endregion
	}
}