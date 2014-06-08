using System;
using HtmlAgilityPack;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Web
{
	public interface IWebDownloader
	{
		string GetText (Uri uri, RequestParams reqParams = null);

		HtmlDocument Get(Uri uri);

        DocumentReponse GetDocument(Uri uri, RequestParams reqParams = null);

		IDictionary<string, string> PostForHeaders (Uri uri, RequestParams reqParams = null);

		IDictionary<string, string> PostForCookies (Uri uri, RequestParams reqParams = null);
	}

    public class DocumentReponse
    {
        internal HtmlDocument Document { get; set; }

        internal IDictionary<string, string> Cookies { get; set; }
    }

	public class RequestParams 
	{
		public IDictionary<string, string> Form { get; set; }
		public IDictionary<string, string> Cookies { get; set; }

        public Uri Referer { get; set; }
    }
}