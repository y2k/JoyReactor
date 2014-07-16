using JoyReactor.Core.Model.Image;
using System;

namespace JoyReactor.Core.Model
{
	public interface IImageModel
	{
		void Load(object token, Uri originalUri, int maxWidth, Action<ImageWrapper> imageCallback);

        string CreateThumbnailUrl(string url, int px);
	}
}