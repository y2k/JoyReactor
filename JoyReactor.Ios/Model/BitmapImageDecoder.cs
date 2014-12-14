using MonoTouch.UIKit;
using XamarinCommons.Image;

namespace JoyReactor.Ios.Model
{
	public class BitmapImageDecoder : ImageDecoder
	{
		public override object Decode (PCLStorage.IFile file)
		{
			return new UIImage (file.Path);
		}

		public override int GetImageSize (object commonImage)
		{
			var image = (UIImage)commonImage;
			if (image == null)
				return 0;
			return (int)(image.Size.Width * image.Size.Height * 4);
		}
	}
}