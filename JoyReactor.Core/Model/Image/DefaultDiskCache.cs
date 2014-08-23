using System;
using PCLStorage;
using System.IO;
using JoyReactor.Core.Model.Inject;
using System.Threading;
using Microsoft.Practices.ServiceLocation;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Image
{
    public class DefaultDiskCache : IDiskCache
    {
        private IImageDecoder decoder = ServiceLocator.Current.GetInstance<IImageDecoder>();
        private IFolder root;

        public DefaultDiskCache()
        {
            root = FileSystem.Current.LocalStorage;
        }

        #region IDiskCache implementation

        public async Task<ImageWrapper> GetAsync(Uri uri)
        {
            var name = ConvertUriToFilename(uri);
            if (await root.CheckExistsAsync(name) == ExistenceCheckResult.NotFound) return null;
            var f = await root.GetFileAsync(name);

            return new ImageWrapper { Image = decoder.Decode(f) };
        }

        public ImageWrapper Get(Uri uri)
        {
            var name = ConvertUriToFilename(uri);
            if (root.CheckExistsAsync(name).Result == ExistenceCheckResult.NotFound) return null;
            var f = root.GetFileAsync(name).Result;

            //using (var stream = f.OpenAsync (FileAccess.Read).Result) {
            //    var i = decoder.Decode (f.Path, stream);
            //    return new ImageWrapper { Image = i };
            //}
            return new ImageWrapper { Image = decoder.Decode(f) };
        }

        public void Put(Uri uri, Stream image)
        {
            var file = ConvertUriToFilename(uri);
            if (root.CheckExistsAsync(file).Result == ExistenceCheckResult.FileExists)
                return;

            var tn = Guid.NewGuid() + ".tmp";
            var tmp = root.CreateFileAsync(tn, CreationCollisionOption.ReplaceExisting).Result;
            using (var outs = tmp.OpenAsync(FileAccess.ReadAndWrite).Result)
            {
                var buf = new byte[4 * 1024];
                int count;
                while ((count = image.Read(buf, 0, buf.Length)) > 0)
                {
                    outs.Write(buf, 0, count);
                }
            }

            try
            {
                tmp.RenameAsync(file).Wait();
            }
            catch
            {
                tmp.DeleteAsync().Wait();
            }
        }

        #endregion

        private string ConvertUriToFilename(Uri uri)
        {
            return uri.GetHashCode() + ".bin";
        }
    }
}