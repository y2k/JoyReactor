using System;
using JoyReactor.Core.Model.Web;
using System.Collections.Generic;
using System.IO;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.Inner
{
	public class MockWebDownloader : IWebDownloader
	{
		private static readonly IDictionary<string, string> RouteUrls = new Dictionary<string, string> () {
			{ "http://m2-ch.ru/a/res/1755718.html", "response1.txt" },
			{ "http://joyreactor.cc/post/1323757", "response2.html" },
			{ "http://joyreactor.cc/post/1382511", "response3.html" },
			{ "http://joyreactor.cc/post/861529", "resp4.html" },
			{ "https://boards.4chan.org/b/", "resp_4chan_b.html" },
			{ "https://boards.4chan.org/b/2", "resp_4chan_b_page2.html" },
			{ "https://boards.4chan.org/wsg/","resp_4chan_wsg.html" },
			{ "https://boards.4chan.org/b/thread/572092321","resp_4chan_thread_572092321.html" },
		};

		#region IWebDownloader implementation

		public Stream GetResource (Uri uri, RequestParams reqParams = null)
		{
			return File.OpenRead (GetPathToFile (uri));
		}

		public string GetText (Uri uri, RequestParams reqParams = null)
		{
			return File.ReadAllText (GetPathToFile (uri));
		}

		public DocumentReponse GetDocument (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
		}

		public System.Collections.Generic.IDictionary<string, string> PostForHeaders (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
		}

		public System.Collections.Generic.IDictionary<string, string> PostForCookies (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
		}

		#endregion

		private static string GetPathToFile (Uri uri)
		{
			var textUrl = "" + uri;
			string name;
			Assert.IsTrue (
				RouteUrls.TryGetValue (textUrl, out name),
				"Can't find path for URL = " + textUrl);
			var path = Path.Combine (@"FakeWebResponses", name);
			Assert.IsTrue (File.Exists (path), "File not found at path " + path);
			return path;
		}
	}
}