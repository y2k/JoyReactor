using System;
using XamarinCommons.Image;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model
{
	public class ImageModel
	{
		ImageDownloader imageDownloader = new ImageDownloader {
			Decoder = ServiceLocator.Current.GetInstance<ImageDecoder> (),
			DiskCache = new DefaultDiskCache (),
			MemoryCache = new DefaultMemoryCache (),
		};

		public async void Load (object token, Uri originalUri, int maxWidth, Action<object> callback)
		{
			var image = await imageDownloader.LoadAsync (token, CreateThumbnailUrl (originalUri, maxWidth));
			if (image != ImageDownloader.InvalideImage)
				callback (image);
		}

		public string CreateThumbnailUrl (string url, int px)
		{
			return "" + CreateThumbnailUrl (new Uri (url), px);
		}

		Uri CreateThumbnailUrl (Uri url, int px)
		{
			if (px == 0)
				return url;
	
			var s = string.Format (
				        "http://remote-cache.api-i-twister.net/Cache/Get?maxHeight=500&width={0}&url={1}",
				        px, Uri.EscapeDataString ("" + url));
			return new Uri (s);
		}
	}
}