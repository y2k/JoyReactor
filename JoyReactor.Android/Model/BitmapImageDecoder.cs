using System;
using JoyReactor.Core.Model.Image;
using System.IO;
using Android.Graphics;

namespace JoyReactor.Android.Model
{
	public class BitmapImageDecoder : IImageDecoder
	{
		#region ImageDecoder implementation

		public object Decode (PCLStorage.IFile file)
		{
			using (var s = file.OpenAsync (PCLStorage.FileAccess.Read).Result) {
				return BitmapFactory.DecodeStream (s);
			}
		}

		public int GetImageSize (ImageWrapper commonImage)
		{
			return
				commonImage == null || commonImage.Image == null
				? 0 : ((Bitmap)commonImage.Image).ByteCount;
		}

		#endregion
	}
}