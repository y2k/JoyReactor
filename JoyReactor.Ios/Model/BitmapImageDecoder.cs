using System;
using JoyReactor.Core.Model.Image;

namespace JoyReactor.Ios.Model
{
	public class BitmapImageDecoder : IImageDecoder
	{
		#region IImageDecoder implementation

		public object Decode (System.IO.Stream stream)
		{
			throw new NotImplementedException ();
		}

		public int GetImageSize (ImageWrapper commonImage)
		{
			throw new NotImplementedException ();
		}

		#endregion
	}
}