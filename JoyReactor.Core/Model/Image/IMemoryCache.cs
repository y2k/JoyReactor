using System;

namespace JoyReactor.Core.Model.Image
{
	public interface IMemoryCache
	{
		ImageWrapper Get(Uri uri);

		void Put(Uri uri, ImageWrapper image);
	}
}