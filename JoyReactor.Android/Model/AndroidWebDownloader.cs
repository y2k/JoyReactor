using System;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Web;
using Java.Net;

namespace JoyReactor.Android.Model
{
    public class AndroidWebDownloader : WebDownloader
    {
        public override Task<WebResponse> ExecuteAsync(Uri uri, RequestParams reqParams)
        {
            return Task.Run(() => Execute(uri, reqParams));
        }

        WebResponse Execute(Uri uri, RequestParams reqParams)
        {
            var connection = new URL(uri.AbsoluteUri).OpenConnection();
            connection.SetRequestProperty("User-Agent", UserAgent);
            connection.SetRequestProperty("Accept", Accept);

            if (reqParams != null)
            {
                if (reqParams.Referer != null)
                    connection.SetRequestProperty("Referer", reqParams.Referer.AbsoluteUri);
            }

            return new WebResponse
            {
                Stream = connection.InputStream,
                ContentLength = connection.ContentLength,
                ResponseUri = new Uri("" + connection.URL),
            };
        }
    }
}