using System.IO;
using Android.Graphics;
using XamarinCommons.Image;

namespace JoyReactor.Android.Model
{
	public class BitmapImageDecoder : ImageDecoder
	{
		public override int GetImageSize (object commonImage)
		{
			return commonImage == null ? 0 : ((Bitmap)commonImage).ByteCount;
		}

		public override object DecoderStream (Stream stream)
		{
			return BitmapFactory.DecodeStream (stream);
		}
	}
}