using PCLStorage;
using System;
using System.IO;

namespace JoyReactor.Core.Model.Image
{
	public interface IImageDecoder
	{
		object Decode(IFile file);

		int GetImageSize(ImageWrapper commonImage);
	}
}