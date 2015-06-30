using System;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Images;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.ViewModels.Common;
using PCLStorage;

namespace JoyReactor.Core.ViewModels
{
    public class GalleryViewModel : ScopedViewModel
    {
        public string ImagePath { get { return Get<string>(); } set { Set(value); } }

        public int Progress { get { return Get<int>(); } set { Set(value); } }

        public bool IsVideo { get { return CheckIsVideo(GetImageUri()); } }

        bool isActivated;

        public async override void OnActivated()
        {
            base.OnActivated();

            if (!isActivated)
            {
                isActivated = true;
                ImagePath = (await DownloadAsync()).Path;
                Progress = 100;
            }
        }

        async Task<IFile> DownloadAsync()
        {
            var targetDir = await FileSystem.Current.LocalStorage.CreateFolderAsync("full-images", CreationCollisionOption.OpenIfExists);
            var targetName = GetTargetName();
            if (await targetDir.CheckExistsAsync(targetName) != ExistenceCheckResult.FileExists)
            {
                var temp = await targetDir.CreateFileAsync(Guid.NewGuid() + "tmp", CreationCollisionOption.ReplaceExisting);
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
                                Progress = Math.Min(99, (int)(100f * totalCopied / response.ContentLength));
                                lastUpdateProgress = Environment.TickCount;
                            }
                        }
                    }
                }
                await temp.RenameAsync(GetTargetName());
            }
            return await targetDir.GetFileAsync(GetTargetName());
        }

        string GetTargetName()
        {
            return GetImageUri().GetHashCode() + ".jpeg";
        }

        Task<WebResponse> CreateImageRequest()
        {
            return new WebDownloader().ExecuteAsync(
                GetImageUri(),
                new RequestParams { Referer = new Uri("http://joyreactor.cc/") });
        }

        Uri GetImageUri()
        {
            var original = new Uri(BaseNavigationService.Instance.GetArgument<string>());
            return  CheckIsVideo(original) 
                ? original
                : new BaseImageRequest.ThumbnailUri(original).ToUri();
        }

        static bool CheckIsVideo(Uri original)
        {
            return original.ToString().EndsWith(".mp4");
        }

        public static bool IsCanShow(string imageUrl)
        {
            return imageUrl != null && (new [] { ".jpeg", ".jpg", ".png", ".mp4" }.Any(imageUrl.EndsWith));
        }
    }
}