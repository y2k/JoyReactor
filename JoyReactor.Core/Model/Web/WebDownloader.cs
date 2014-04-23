using System;
using HtmlAgilityPack;
using System.Collections.Generic;
using System.Net;
using System.Threading;
using System.Net.Http;

namespace JoyReactor.Core.Model.Web
{
	public class WebDownloader : IWebDownloader
	{
		#region IWebDownloader implementation

		public string GetText (Uri uri, RequestParams reqParams = null)
		{
//			return new HttpClient ().GetAsync (uri).Result.Content.ReadAsStringAsync ().Result;
			return new HttpClient ().GetStringAsync (uri).Result;
		}

		public HtmlAgilityPack.HtmlDocument Get (Uri uri)
		{
			throw new NotImplementedException ();
		}

		public IDictionary<string, string> PostForHeaders (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
		}

		#endregion
	}
}