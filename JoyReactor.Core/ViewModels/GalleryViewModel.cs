using System;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Common;
using JoyReactor.Core.Model.Images;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.ViewModels.Common;
using Microsoft.Practices.ServiceLocation;
using PCLStorage;

namespace JoyReactor.Core.ViewModels
{
    public class GalleryViewModel : ScopedViewModel
    {
        public string ImagePath { get { return Get<string>(); } set { Set(value); } }

        public int Progress { get { return Get<int>(); } set { Set(value); } }

        public bool IsVideo { get { return GetImageUrl().IsVideo; } }

        bool isActivated;

        public async override void OnActivated()
        {
            base.OnActivated();

            if (!isActivated)
            {
                isActivated = true;
                await Initialize();
            }
        }

        async Task Initialize()
        {
            ImagePath = await new Downloader
            {
                ImageUrl = GetImageUrl().ToUri(),
                ProgressCallback = s => Progress = s,
            }.RequestImageAsync();
            Progress = 100;
        }

        ImageUrl GetImageUrl()
        {
            return new ImageUrl { OriginalUrl = BaseNavigationService.Instance.GetArgument<string>() };
        }

        public static bool IsCanShow(string imageUrl)
        {
            return imageUrl != null && (new[] { ".jpeg", ".jpg", ".png", ".mp4", ".gif" }.Any(imageUrl.EndsWith));
        }

        class ImageUrl
        {
            internal string OriginalUrl { get; set; }

            internal bool IsVideo
            { 
                get { return new[] { ".mp4", ".gif" }.Any(OriginalUrl.EndsWith); }
            }

            internal Uri ToUri()
            {
                var uri = new Uri(OriginalUrl);
                return OriginalUrl.EndsWith(".mp4") 
                    ? uri
                    : new BaseImageRequest.ThumbnailUri(uri).SetFormat("mp4").ToUri();
            }
        }

        class Downloader
        {
            internal Uri ImageUrl { get; set; }

            internal Action<int> ProgressCallback;

            IFolder imageFolder;

            internal async Task<string> RequestImageAsync()
            {
                imageFolder = await FileSystem.Current.LocalStorage.CreateFolderAsync(
                    "full-images", CreationCollisionOption.OpenIfExists);
                if (await imageFolder.CheckExistsAsync(GetTargetName()) != ExistenceCheckResult.FileExists)
                    await Download();
                var file = await imageFolder.GetFileAsync(GetTargetName());
                return file.Path;
            }

            async Task Download()
            {
                for (int i = 0; i < 5; i++)
                {
                    try
                    {
                        await TryDownloader();
                        break;
                    }
                    catch (NotFoundException)
                    {
                        await Task.Delay(500 << i);
                    }
                }
            }

            async Task TryDownloader()
            {
                var temp = await imageFolder.CreateFileAsync(Guid.NewGuid() + "tmp", CreationCollisionOption.ReplaceExisting);
                using (var response = await CreateImageRequest())
                {
                    using (var targetStream = await temp.OpenAsync(FileAccess.ReadAndWrite))
                    {
                        int lastUpdateProgress = 0;
                        var buf = new byte[4 * 1024];
                        int count, totalCopied = 0;
                        while ((count = await response.Stream.ReadAsync(buf, 0, buf.Length)) != 0)
                        {
                            await targetStream.WriteAsync(buf, 0, count);
                            totalCopied += count;
                            if (Environment.TickCount - lastUpdateProgress > 1000 / 60)
                            {
                                ProgressCallback(Math.Min(99, (int)(100f * totalCopied / response.ContentLength)));
                                lastUpdateProgress = Environment.TickCount;
                            }
                        }
                    }
                }
                await temp.RenameAsync(GetTargetName());
            }

            Task<WebResponse> CreateImageRequest()
            {
                var client = ServiceLocator.Current.GetInstance<WebDownloader>();
                return client.ExecuteAsync(ImageUrl, new RequestParams { Referer = new Uri("http://joyreactor.cc/") });
            }

            string GetTargetName()
            {
                return ImageUrl.GetHashCode() + ".jpeg";
            }
        }
    }
}