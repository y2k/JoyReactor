using System;
using System.IO;
using System.Threading;
using System.Threading.Tasks;
using PCLStorage;

namespace JoyReactor.Core.Model.Images
{
    public class DiskCache
    {
        readonly ImageFolder imageFolder = new ImageFolder();
        SemaphoreSlim oneThreadLocker = new SemaphoreSlim(1);

        public async Task<byte[]> GetAsync(Uri uri)
        {
            await oneThreadLocker.WaitAsync();
            try
            {
                var folder = await imageFolder.GetAsync();
                if (await folder.CheckExistsAsync("" + uri.GetHashCode()) != ExistenceCheckResult.FileExists)
                    return null;

                var file = await folder.GetFileAsync("" + uri.GetHashCode());
                var buffer = new byte[8 * 1024];
                var result = new MemoryStream();
                using (var stream = await file.OpenAsync(FileAccess.ReadAndWrite))
                {
                    int count = 0;
                    while ((count = await stream.ReadAsync(buffer, 0, buffer.Length)) > 0)
                        result.Write(buffer, 0, count);
                }
                return result.ToArray();
            }
            finally
            {
                oneThreadLocker.Release();
            }
        }

        public async Task PutAsync(Uri uri, byte[] data)
        {
            await oneThreadLocker.WaitAsync();
            try
            {
                var folder = await imageFolder.GetAsync();
                var file = await folder.CreateFileAsync("" + uri.GetHashCode(), CreationCollisionOption.ReplaceExisting);
                using (var stream = await file.OpenAsync(FileAccess.ReadAndWrite))
                    await stream.WriteAsync(data, 0, data.Length);
            }
            finally
            {
                oneThreadLocker.Release();
            }
        }

        Task<IFolder> GetFolder()
        {
            return imageFolder.GetAsync();
        }

        class ImageFolder
        {
            IFolder folder;

            public async Task<IFolder> GetAsync()
            {
                if (folder == null)
                    folder = await CreateFolder();
                return folder;
            }

            static Task<IFolder> CreateFolder()
            {
                return FileSystem.Current.LocalStorage.CreateFolderAsync(
                    "images", CreationCollisionOption.OpenIfExists);
            }
        }
    }
}