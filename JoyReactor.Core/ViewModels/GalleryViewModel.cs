using System;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.ViewModels.Common;
using PCLStorage;

namespace JoyReactor.Core.ViewModels
{
    public class GalleryViewModel : ScopedViewModel
    {
        public string ImagePath { get { return Get<string>(); } set { Set(value); } }

        public int Progress { get { return Get<int>(); } set { Set(value); } }

        public async override void OnActivated()
        {
            base.OnActivated();

            var file = await DownloadAsync();
            Progress = 100;
            ImagePath = file.Path;
        }

        async Task<IFile> DownloadAsync()
        {
            var targetDir = await FileSystem.Current.LocalStorage.CreateFolderAsync("full-images", CreationCollisionOption.OpenIfExists);
            var targetName = GetTargetName();
            if (await targetDir.CheckExistsAsync(targetName) != ExistenceCheckResult.FileExists)
            {
                var targetFile = await targetDir.CreateFileAsync(GetTargetName(), CreationCollisionOption.OpenIfExists);
                using (var response = await new WebDownloader().ExecuteAsync(new Uri(GetImageUrl())))
                {
                    using (var targetStream = await targetFile.OpenAsync(FileAccess.ReadAndWrite))
                    {
                        var buf = new byte[16 * 1024];
                        int count, totalCopied = 0;
                        while ((count = await response.Stream.ReadAsync(buf, 0, buf.Length)) != 0)
                        {
                            await targetStream.WriteAsync(buf, 0, count);
                            totalCopied += count;
                            Progress = Math.Min(99, (int)(100f * totalCopied / response.ContentLength));
                        }
                    }
                }
            }
            return await targetDir.GetFileAsync(GetTargetName());
        }

        string GetTargetName()
        {
            return GetImageUrl().GetHashCode() + ".jpeg";
        }

        string GetImageUrl()
        {
            return BaseNavigationService.Instance.GetArgument<string>();
        }
    }
}