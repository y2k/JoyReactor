using System;
using JoyReactor.Core.Model.Web;
using HtmlAgilityPack;

namespace JoyReactor.Core.Model.Helper
{
	public static class WebDownloaderExtensions
	{
		public static HtmlDocument Get(this IWebDownloader instance, Uri uri) {
			using (var s = instance.GetResource(uri)) {
				var doc = new HtmlDocument();
				doc.Load(s);
				return doc;
			}
		}
	}
}