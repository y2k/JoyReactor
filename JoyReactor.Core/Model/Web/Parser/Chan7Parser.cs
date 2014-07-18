using HtmlAgilityPack;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Web.Parser
{
    public class Chan7Parser : ISiteParser
    {
        private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

        public ID.SiteParser ParserId
        {
            get { return ID.SiteParser.Chan7; }
        }

        public IDictionary<string, string> Login(string username, string password)
        {
            throw new NotImplementedException();
        }

        public void ExtractTagPostCollection(ID.TagType type, string tag, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
            var baseUrl = new Uri(string.Format("http://7chan.org/{0}/{1}", Uri.EscapeDataString(tag), lastLoadedPage < 1 ? "" : lastLoadedPage + ".html"));
            var doc = downloader.GetDocument(baseUrl).Document.DocumentNode;

            callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });

            foreach (var node in doc.Select("div.op > div"))
            {
                var p = new ExportPost();

                p.id = tag.Trim().ToLower() + "," + node.Id;
                p.userName = node.Select("span.postername").First().InnerText;

                var titles = node.Select("span.subject");
                if (titles.Count() > 0) p.title = titles.First().InnerText;
                titles = node.Select("p.message");
                if (titles.Count() > 0) p.title = HtmlEntity.DeEntitize(titles.First().InnerText.ShortString(100).Trim('\r', '\n', ' '));
                if (string.IsNullOrEmpty(p.title)) p.title = null;

                var m = Regex.Match(node.InnerHtml, "<a href=\"([^\"]+)\" id=\"expandimg_" + node.Id + "_(\\d+)_(\\d+)");
                if (m.Success)
                {
                    p.image = m.Groups[1].Value.Replace("https://", "http://");
                    p.imageWidth = int.Parse(m.Groups[2].Value);
                    p.imageHeight = int.Parse(m.Groups[3].Value);
                }

                m = Regex.Match(node.InnerHtml, "</span>[\r\n]*([^\r\n]+)[\r\n]*<span class=\"reflink\">");
                if (!m.Success) throw new InvalidOperationException("Can't find date-time in post " + p.id);
                p.created = NodeHelper.DateTimeToUnixTimestamp(DateTime.ParseExact(m.Groups[1].Value, "yy/MM/dd(ddd)HH:mm", CultureInfo.InvariantCulture)) * 1000L;

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
    }
}