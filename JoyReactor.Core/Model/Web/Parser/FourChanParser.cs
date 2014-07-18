using System;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;
using System.Linq;
using JoyReactor.Core.Model.Parser.Data;
using System.Text.RegularExpressions;
using HtmlAgilityPack;

namespace JoyReactor.Core.Model.Web.Parser
{
    public class FourChanParser : ISiteParser
    {
        private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

        #region ISiteParser implementation

        public IDictionary<string, string> Login(string username, string password)
        {
            throw new NotImplementedException();
        }

        public void ExtractTagPostCollection(ID.TagType type, string tag, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
            var baseUrl = new Uri(string.Format("http://boards.4chan.org/{0}/{1}", Uri.EscapeDataString(tag), lastLoadedPage));
            var doc = downloader.GetDocument(baseUrl).Document.DocumentNode;

            callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });

            foreach (var node in doc.Select("div.thread"))
            {
                var p = new ExportPost();

                var links = node.Select("a.replylink");
                var m = Regex.Match(links.First().AbsUrl(baseUrl, "href"), "/thread/(\\d+)");
                if (m.Success) p.id = m.Groups[1].Value + "," + m.Groups[2].Value;
                else throw new InvalidOperationException("Can't find post id");

                var imgs = node.Select("a.fileThumb");
                if (imgs.Count() > 0) p.image = imgs.First().AbsUrl(baseUrl, "href");

                var titles = node.Select("span.subject");
                if (titles.Count() > 0) p.title = titles.First().InnerText;
                titles = node.Select("blockquote.postMessage");
				if (titles.Count() > 0) p.title = HtmlEntity.DeEntitize(titles.First().InnerText.ShortString(100));
                if (string.IsNullOrEmpty(p.title)) p.title = null;

                var dates = node.Select("span.dateTime");
                p.created = long.Parse(dates.First().Attr("data-utc")) * 1000;

                p.userName = node.Select("span.nameBlock span.name").First().InnerText;

                callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
            }
        }

        public void ExtractPost(string postId, Action<PostExportState> callback)
        {
            throw new NotImplementedException();
        }

        public ProfileExport Profile(string username)
        {
            throw new NotImplementedException();
        }

        public ID.SiteParser ParserId
        {
            get { return ID.SiteParser.Chan4; }
        }

        #endregion
    }
}