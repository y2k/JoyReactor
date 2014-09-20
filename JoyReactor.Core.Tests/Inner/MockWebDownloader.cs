using System;
using JoyReactor.Core.Model.Web;
using System.Collections.Generic;
using System.IO;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.Inner
{
	public class MockWebDownloader : IWebDownloader
	{
		private static readonly IDictionary<string, string> RouteUrls = new Dictionary<string, string>() {
			{ "http://m2-ch.ru/a/res/1755718.html", "response1.txt" },
		};

		#region IWebDownloader implementation

		public System.IO.Stream GetResource (Uri uri, RequestParams reqParams = null)
		{
			var path = Path.Combine(@"FakeWebResponses", RouteUrls ["" + uri]);
			Assert.IsTrue (File.Exists (path), "File not found at path " + path);
			return File.OpenRead (path);
		}

		public string GetText (Uri uri, RequestParams reqParams = null)
		{
			throw new NotImplementedException ();
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
	}
}