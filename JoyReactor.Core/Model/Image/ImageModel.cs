using System;
using System.Threading.Tasks;
using System.Net.Http;
using System.IO;
using PCLStorage;
using System.Threading;
using Ninject;
using JoyReactor.Core.Model.Inject;

namespace JoyReactor.Core.Model.Image
{
	public class ImageModel : IImageModel
	{
		private const int MaxAttempts = 5;
		private const int BaseAttemptDelay = 500;

		private IMemoryCache memoryCache = InjectService.Instance.Get<IMemoryCache>();
		private IDiskCache diskCachge = InjectService.Instance.Get<IDiskCache> ();
		private HttpClient webClient = new HttpClient();

		#region IImageModel implementation

		public async void Load (object token, Uri originalUri, int maxWidth, Action<ImageWrapper> imageCallback)
		{
			var uri = CreateThumbnailUrl (originalUri, maxWidth);

			// Поиск картинки в кэше памяти
			var mi = memoryCache.Get (uri);
			if (mi != null) {
				imageCallback (mi);
				return;
			}

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
				// Запрос к диску в фоновом потоке
				var i = await Task.Run<ImageWrapper>(() => diskCachge.Get(uri));
				if (i != null) {
					memoryCache.Put (uri, i);
					imageCallback(i);
					return;
				}
			}

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