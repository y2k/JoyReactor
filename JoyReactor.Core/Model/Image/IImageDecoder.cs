using System;
using System.IO;

namespace JoyReactor.Core.Model.Image
{
	public interface IImageDecoder
	{
		object Decode(Stream stream);

		int GetImageSize(ImageWrapper commonImage);
	}
}