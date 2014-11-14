using System;
using System.IO;
using XamarinCommons.Image;

namespace JoyReactor.Ios.Model
{
	public class BitmapImageDecoder : ImageDecoder
	{
		public override object DecoderStream (Stream stream)
		{
			throw new NotImplementedException ();
		}

		public override int GetImageSize (object commonImage)
		{
			throw new NotImplementedException ();
		}
	}
}