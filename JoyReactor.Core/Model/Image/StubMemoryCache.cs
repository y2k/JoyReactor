using System;

namespace JoyReactor.Core.Model.Image
{
	public class StubMemoryCache : IMemoryCache
	{
		#region IMemoryCache implementation

		public ImageWrapper Get (Uri uri)
		{
			return null;
		}

		public void Put (Uri uri, ImageWrapper image)
		{
			// Nothing to do
		}

		#endregion
	}
}