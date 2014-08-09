using JoyReactor.Core.Model.Image;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.Model
{
    class PathMemoryCache : IMemoryCache
    {
        private IDictionary<Uri, ImageWrapper> map = new Dictionary<Uri, ImageWrapper>();

        public ImageWrapper Get(Uri uri)
        {
            lock (map)
            {
                ImageWrapper r = null;
                return map.TryGetValue(uri, out r) ? r : null;
            }
        }

        public void Put(Uri uri, ImageWrapper image)
        {
            lock (map)
            {
                map[uri] = image;
            }
        }
    }
}