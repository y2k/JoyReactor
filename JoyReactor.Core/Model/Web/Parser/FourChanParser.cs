using System;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;
using System.Linq;

namespace JoyReactor.Core.Model.Web.Parser
{
	public class FourChanParser : ISiteParser
	{
		private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

		#region ISiteParser implementation

		public IDictionary<string, string> Login (string username, string password)
		{
			throw new NotImplementedException ();
		}

		public void ExtractTagPostCollection (ID.TagType type, string tag, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
		{
			var url = string.Format("http://boards.4chan.org/%s/%s", Uri.EscapeDataString(tag), lastLoadedPage);
			var doc = downloader.GetDocument(new Uri(url));

			callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });

//			var z = doc.Document.DocumentNode.Descendants ().Where (s => s.Name = "div" && s.Attributes.Any (a => a.Name = "class" && a.Value.Contains ("thread"))).ToList ();
//
//			List<Post> posts = new ArrayList<Post>();
//			foreach (Element node in doc.select("div.thread")) {
//				Post p = new Post();
//
//				Elements links = node.select("a.replylink");
//				Matcher m = Pattern.compile("([^/]+)/res/(\\d+)").matcher(links.first().absUrl("href"));
//				if (m.find()) p.serverId = m.group(1) + "," + m.group(2);
//				else throw new IllegalStateException("Can't find post id");
//
//				Elements imgs = node.select("a.fileThumb");
//				if (!imgs.isEmpty()) p.image = imgs.first().absUrl("href");
//
//				Elements es = node.select("a.fileThumb > img");
//				if (!es.isEmpty()) p.thumbnail = es.first().absUrl("src");
//
//				Elements titles = node.select("span.subject");
//				if (!titles.isEmpty()) p.title = titles.first().text();
//				if (TextUtils.isEmpty(p.title)) p.title = null;
//
//				Elements dates = node.select("span.dateTime");
//				p.updated = new Date(Long.parseLong(dates.attr("data-utc")) * 1000);
//
//				p.username = node.select("span.nameBlock span.name").first().text();
//
//				posts.add(p);
//			}
//
//			return posts;
		}

		public void ExtractPost (string postId, Action<PostExportState> callback)
		{
			throw new NotImplementedException ();
		}

		public ProfileExport Profile (string username)
		{
			throw new NotImplementedException ();
		}

		public ID.SiteParser ParserId {
			get {
				throw new NotImplementedException ();
			}
		}

		#endregion
	}
}