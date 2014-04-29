using System;
using System.Linq;
using HtmlAgilityPack;
using System.Collections.Generic;
using System.Net;
using System.Threading;
using System.Net.Http;

namespace JoyReactor.Core.Model.Web
{
	public class WebDownloader : IWebDownloader
	{
		private Lazy<HttpClient> client = new Lazy<HttpClient> (() => {
			var h = new HttpClientHandler() {
//				Proxy = new WebProxy("http://127.0.0.1:8888"),
//				UseProxy = true,
			};
			return new HttpClient (h);
		});

		#region IWebDownloader implementation

		public string GetText (Uri uri, RequestParams reqParams = null)
		{
			return client.Value.GetStringAsync (uri).Result;
		}

		public HtmlDocument Get (Uri uri)
		{

			var doc = new HtmlDocument ();
			using (var s = client.Value.GetStreamAsync (uri).Result) {
				doc.Load (s);
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
			var r = client.Value.PostAsync (uri, content).Result;

			var h = r.Headers;
			h.ToString ();

			throw new NotImplementedException ();
		}

		#endregion
	}
}