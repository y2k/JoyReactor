using System;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Image;

namespace JoyReactor.Core.Model
{
	public interface IImageModel
	{
		void Load(object token, Uri originalUri, int maxWidth, Action<ImageWrapper> imageCallback);
	}
}