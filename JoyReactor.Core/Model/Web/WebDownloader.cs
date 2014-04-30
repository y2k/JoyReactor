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

		private CookieContainer cookies = new CookieContainer();
		private Lazy<HttpClient> client;

		public WebDownloader()
		{
			this.client = new Lazy<HttpClient> (() => {
				var client = new HttpClient (new HttpClientHandler() { CookieContainer = cookies });
				client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
				client.DefaultRequestHeaders.Accept.ParseAdd(Accept);
				return client;
			});
		}

		#region IWebDownloader implementation

		public string GetText (Uri uri, RequestParams reqParams = null)
		{
			return client.Value.GetStringAsync (uri).Result;
		}

		public HtmlDocument Get (Uri uri)
		{
			var doc = new HtmlDocument ();
			using (var r = client.Value.GetAsync (uri).Result) {
				using (var s = r.Content.ReadAsStreamAsync ().Result) {
					doc.Load (s);
				}
			}
			return doc;
		}

		public IDictionary<string, string> PostForHeaders (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
		}

		public IDictionary<string, string> PostForCookies (Uri uri, RequestParams reqParams = null)
		{
			if (reqParams == null || reqParams.Form == null)
				throw new Exception ();

			var content = new FormUrlEncodedContent (reqParams.Form);
			using (var r = client.Value.PostAsync (uri, content).Result) { 
				// Nothing todo
			}

			return cookies.GetCookies (uri).Cast<Cookie> ().ToDictionary (s => s.Name, s => s.Value);
		}

		#endregion
	}
}