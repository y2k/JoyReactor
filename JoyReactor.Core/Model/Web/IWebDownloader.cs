using System;
using HtmlAgilityPack;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Web
{
	public interface IWebDownloader
	{
		string GetText (Uri uri, RequestParams reqParams = null);

		HtmlDocument Get(Uri uri);

		IDictionary<string, string> PostForHeaders (Uri uri, RequestParams reqParams = null);
	}

	public class RequestParams 
	{
		public IDictionary<string, string> Form { get; set; }
		public IDictionary<string, string> Cookies { get; set; }
	}
}