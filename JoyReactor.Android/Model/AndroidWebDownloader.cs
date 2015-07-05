using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Java.Net;
using JoyReactor.Core.Model.Web;
using System.IO;

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
            var connection = (HttpURLConnection)new URL(uri.AbsoluteUri).OpenConnection();
            connection.SetRequestProperty("User-Agent", UserAgent);
            connection.SetRequestProperty("Accept", Accept);

            if (reqParams != null)
            {
                connection.InstanceFollowRedirects = !reqParams.NotFollowRedirects;

                if (reqParams.Referer != null)
                    connection.SetRequestProperty("Referer", reqParams.Referer.AbsoluteUri);
                if (reqParams.Cookies != null && reqParams.Cookies.Count > 0)
                {
                    var cookie = reqParams.Cookies
                        .Select(s => s.Key + "=" + s.Value)
                        .Aggregate((a, s) => a + "; " + s);
                    connection.SetRequestProperty("Cookie", cookie);
                }
                if (reqParams.Form != null)
                {
                    connection.RequestMethod = "POST";
                    var form = reqParams.Form
                        .Select(s => Uri.EscapeDataString(s.Key) + "=" + Uri.EscapeDataString(s.Value))
                        .Aggregate((a, s) => a + "&" + s);
                    var buffer = Encoding.UTF8.GetBytes(form);
                    connection.OutputStream.Write(buffer, 0, buffer.Length);
                }
            }

            var r = new WebResponse
            {
                Stream = GetStream(connection),
                ContentLength = connection.ContentLength,
                ResponseUri = new Uri("" + connection.URL),
                Cookies = GetCookies(connection),
            };
            return r;
        }

        IDictionary<string, string> GetCookies(URLConnection connection)
        {
            return connection.HeaderFields.Keys
                .Where(s => s == "Set-Cookie")
                .SelectMany(s => connection.HeaderFields[s])
                .SelectMany(s => HttpCookie.Parse(s))
                .ToDictionary(s => s.Name, s => s.Value);
        }

        static Stream GetStream(HttpURLConnection connection)
        {
            return connection.ResponseCode != HttpStatus.Unauthorized
                ? connection.InputStream : connection.ErrorStream;
        }
    }
}