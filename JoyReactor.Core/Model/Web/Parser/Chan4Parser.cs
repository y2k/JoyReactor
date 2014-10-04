using System;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;
using System.Linq;
using JoyReactor.Core.Model.Parser.Data;
using System.Text.RegularExpressions;
using HtmlAgilityPack;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Core.Model.Web.Parser
{
	public class Chan4Parser : ISiteParser
	{
		private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader> ();

		#region ISiteParser implementation

		public IDictionary<string, string> Login (string username, string password)
		{
			throw new NotImplementedException ();
		}

		public void ExtractTagPostCollection (ID.TagType type, string tag, int currentPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
		{
			var pagePostfix = currentPage == 0 ? "" : "" + (currentPage + 1);
			var escapedTag = Uri.EscapeDataString (tag);
			var baseUrl = new Uri (string.Format ("https://boards.4chan.org/{0}/{1}", escapedTag, pagePostfix));
			var doc = downloader.Get (baseUrl).DocumentNode;

			callback (new CollectionExportState { State = CollectionExportState.ExportState.Begin });
			callback (new CollectionExportState { 
				State = CollectionExportState.ExportState.TagInfo, 
				TagInfo = new ExportTag { nextPage = currentPage + 1 }
			});

			foreach (var node in doc.Select("div.thread")) {
				var p = new ExportPost ();

				var links = node.Select ("a.replylink");
				var m = Regex.Match (links.First ().AbsUrl (baseUrl, "href"), "/thread/(\\d+)");
				if (m.Success)
					p.id = new ThreadId { Board = tag, Id = m.Groups [1].Value }.Pack ();
				else
					throw new InvalidOperationException ("Can't find post id");

				var imgs = node.Select ("a.fileThumb");
				if (imgs.Count () > 0) {
					p.image = imgs.First ().AbsUrl (baseUrl, "href");

					var z = Regex.Match (p.image, "(\\d+)\\.[\\w\\d]+$").Value;
					m = Regex.Match (node.InnerHtml, "(\\d+)x(\\d+)\\)</div><a class=\"fileThumb\" href=\"//[^/]+/[^/]+/" + Regex.Escape (z));
					p.imageWidth = int.Parse (m.Groups [1].Value);
					p.imageHeight = int.Parse (m.Groups [2].Value);
				}

				var titles = node.Select ("span.subject");
				if (titles.Count () > 0)
					p.title = titles.First ().InnerText;
				titles = node.Select ("blockquote.postMessage");
				if (titles.Count () > 0)
					p.title = HtmlEntity.DeEntitize (titles.First ().InnerText.ShortString (100));
				if (string.IsNullOrEmpty (p.title))
					p.title = null;

				var dates = node.Select ("span.dateTime");
				p.created = long.Parse (dates.First ().Attr ("data-utc")) * 1000;

				p.userName = node.Select ("span.nameBlock span.name").First ().InnerText;

				callback (new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
			}
		}

		public void ExtractPost (string postId, Action<PostExportState> callback)
		{
			var thread = ThreadId.Unpack (postId);
			var threadUri = CreateThreadUri (thread);
			var doc = downloader.Get (threadUri);
			callback (new PostExportState { State = PostExportState.ExportState.Begin });

			ExportPostInfo (thread, doc);

			throw new NotImplementedException ();
		}

		public ProfileExport Profile (string username)
		{
			throw new NotImplementedException ();
		}

		public ID.SiteParser ParserId {
			get { return ID.SiteParser.Chan4; }
		}

		#endregion

		private void ExportPostInfo (ThreadId thread, HtmlDocument doc)
		{
			var postDiv = doc.GetElementById ("p" + thread.Id);

			var chunk = new PostExportState ();
		}

		private Uri CreateThreadUri (ThreadId thread)
		{
			var url = string.Format ("https://boards.4chan.org/{0}/thread/{1}", thread.Board, thread.Id);
			return new Uri (url);
		}

		private struct ThreadId
		{
			public string Id { get; set; }

			public string Board { get; set; }

			public static ThreadId Unpack (string packedId)
			{
				var idParts = packedId.Split (',');
				return new ThreadId { Board = idParts [0], Id = idParts [1] };
			}

			public string Pack ()
			{
				return Board + "," + Id;
			}

			public override string ToString ()
			{
				return string.Format ("[ThreadId: Board={0}, Id={1}]", Board, Id);
			}
		}
	}
}