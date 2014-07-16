using System;
using System.Threading.Tasks;
using System.Net.Http;
using System.Linq;
using System.IO;
using PCLStorage;
using System.Threading;
using JoyReactor.Core.Model.Inject;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Image
{
	public class ImageModel : IImageModel
	{
		private const int MaxAttempts = 5;
		private const int BaseAttemptDelay = 500;

        private IMemoryCache memoryCache = ServiceLocator.Current.GetInstance<IMemoryCache>();
        private IDiskCache diskCachge = ServiceLocator.Current.GetInstance<IDiskCache>();
		private HttpClient webClient = new HttpClient();

		private Dictionary<object, Uri> lockedImages = new Dictionary<object, Uri>();

		#region IImageModel implementation

		public async void Load (object token, Uri originalUri, int maxWidth, Action<ImageWrapper> originalImageCallback)
		{
			if (originalUri == null) {
				originalImageCallback(new ImageWrapper());
				lockedImages.Remove(token);
				return;
			}

			lockedImages [token] = originalUri;
			Action<ImageWrapper> imageCallback = image => {
				if (lockedImages.Any(s => s.Key==token && s.Value==originalUri)) {
					originalImageCallback(image);
					lockedImages.Remove(token);
				}
			};

			var uri = CreateThumbnailUrl (originalUri, maxWidth);

			// Поиск картинки в кэше памяти
			var mi = memoryCache.Get (uri);
			if (mi != null) {
				imageCallback (mi);
				return;
			}

			#if ACCESS_TO_DISK_IN_MAIN
			// Поиск картинки в кэше на диске
			if (Math.Abs(1) == 0) { // FIXME
				// Запрос к диску в главном потоке 
				var i = diskCachge.Get (uri);
				if (i != null) {
					memoryCache.Put (uri, i);
					imageCallback(i);
					return;
				}
			} else {
			#endif
				// Запрос к диску в фоновом потоке
				var i = await Task.Run<ImageWrapper>(() => diskCachge.Get(uri));
				if (i != null) {
					memoryCache.Put (uri, i);
					imageCallback(i);
					return;
				}
			#if ACCESS_TO_DISK_IN_MAIN
			}
			#endif

			// Загрузка картинки с вэба
			await Task.Run (
				async () => {

					for (int t = 0; t < MaxAttempts; t++) {
						try {
							using (var ins = await webClient.GetStreamAsync(uri)) {
								diskCachge.Put(uri, ins);
								mi = diskCachge.Get (uri);
								memoryCache.Put (uri, mi);
							}
							return;
						} catch (HttpRequestException) {
							new ManualResetEvent(false).WaitOne(BaseAttemptDelay << t);
						}
					}
				
				});

			imageCallback (mi);
		}

        public string CreateThumbnailUrl(string url, int px)
        {
            return "" + CreateThumbnailUrl(new Uri(url), px);
        }

		#endregion

		#region Private methods

		private Uri CreateThumbnailUrl(Uri url, int px) {
			if (px == 0)
				return url;

			var s = string.Format (
				"http://remote-cache.api-i-twister.net/Cache/Get?maxHeight=500&width={0}&url={1}", 
		        px, Uri.EscapeDataString ("" + url));
			return new Uri(s);
		}

		#endregion
    }
}