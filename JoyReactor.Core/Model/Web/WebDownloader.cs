using System;
using System.Linq;
using HtmlAgilityPack;
using System.Collections.Generic;
using System.Net;
using System.Threading;
using System.Net.Http;
using System.Net.Http.Headers;

namespace JoyReactor.Core.Model.Web
{
	public class WebDownloader : IWebDownloader
	{
		#region Constants

		private const string UserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68";
		private const string Accept = "text/html";

		#endregion

        private static readonly CookieContainer DefaultCookies = new CookieContainer();
        private static readonly Lazy<HttpClient> DefaultClient = new Lazy<HttpClient>(() => {
            var handler = new HttpClientHandler {
                UseCookies = true,
                CookieContainer = DefaultCookies
            };
            if (handler.SupportsAutomaticDecompression) {
                handler.AutomaticDecompression = DecompressionMethods.GZip | DecompressionMethods.Deflate;
            }

            var client = new HttpClient(handler);
            client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
            client.DefaultRequestHeaders.Accept.ParseAdd(Accept);
            return client;
        });

		#region IWebDownloader implementation

		public string GetText (Uri uri, RequestParams reqParams = null)
		{
            return DefaultClient.Value.GetStringAsync(uri).Result;
        }

		public HtmlDocument Get (Uri uri)
		{
            using (var r = DefaultClient.Value.GetAsync(uri).Result)
            {
                using (var s = r.Content.ReadAsStreamAsync().Result) {
                    var doc = new HtmlDocument();
                    doc.Load(s);
                    return doc;
                }
            }
		}

		public IDictionary<string, string> PostForHeaders (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
		}

		public IDictionary<string, string> PostForCookies (Uri uri, RequestParams reqParams = null)
		{
			if (reqParams == null || reqParams.Form == null) throw new Exception ();

            var req = new HttpRequestMessage();
            req.RequestUri = uri;
            req.Method = HttpMethod.Post;
            req.Content = new FormUrlEncodedContent(reqParams.Form);
            if(reqParams.Referer != null) req.Headers.Referrer = reqParams.Referer;

            using (var r = DefaultClient.Value.SendAsync(req).Result) {
                // Nothing todo
            }

            return DefaultCookies.GetCookies(uri).Cast<Cookie>().ToDictionary(s => s.Name, s => s.Value);
		}

		#endregion
	}
}