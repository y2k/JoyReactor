using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using Refractored.Xam.Settings;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Common
{
    abstract class ImageLoader<T>
    {
        WebDownloader downloader = ServiceLocator.Current.GetInstance<WebDownloader>();
        ImageCache cache;
        List<T> items;

        internal ImageLoader(List<T> items, string prefix)
        {
            cache = new ImageCache(prefix);
            this.items = items;
        }

        internal async Task LoadAsync()
        {
            foreach (var s in items)
            {
                var avatar = cache.Get(GetKey(s));
                if (string.IsNullOrEmpty(avatar))
                {
                    avatar = await GetFromWeb(downloader, s);

                    if (!string.IsNullOrEmpty(avatar))
                        cache.Put(GetKey(s), avatar);
                }
                Set(s, avatar);
            }
        }

        protected abstract string GetKey(T item);

        protected abstract Task<string> GetFromWeb(WebDownloader downloader, T item);

        protected abstract void Set(T item, string value);

        class ImageCache
        {
            string prefix;

            internal ImageCache(string prefix)
            {
                this.prefix = prefix;
            }

            internal string Get(string key)
            {
                return CrossSettings.Current.GetValueOrDefault<string>(GetFullKey(key));
            }

            internal void Put(string key, string value)
            {
                CrossSettings.Current.AddOrUpdateValue(GetFullKey(key), value);
            }

            string GetFullKey(string key)
            {
                return prefix + key;
            }
        }
    }
}