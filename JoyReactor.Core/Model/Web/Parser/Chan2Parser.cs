using HtmlAgilityPack;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Web.Parser
{
    public class Chan2Parser : ISiteParser
    {
        private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

        public ID.SiteParser ParserId
        {
            get { return ID.SiteParser.Chan2; }
        }

        public IDictionary<string, string> Login(string username, string password)
        {
            throw new NotImplementedException();
        }

#if USER_FULL_SITE_2_CHAN_FOR_DATA

        // Версия которая запрашивает данные с основного сайт, нельзя пользоваться т.к. SSL ошибки + блокировка clouflare
        public void ExtractTagPostCollection(ID.TagType type, string tag, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
            var baseUrl = new Uri(string.Format("https://2-ch.so/{0}/{1}", Uri.EscapeDataString(tag), lastLoadedPage < 1 ? "" : lastLoadedPage + ".html"));
            var doc = downloader.GetDocument(baseUrl).Document.DocumentNode;

            callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });

            foreach (var node in doc.Select("div.oppost"))
            {
                var p = new ExportPost();

                p.id = tag + "," + Regex.Match(node.Id, "\\d+").Value;
                p.title = node.Select("span.filetitle").First().InnerText;
                p.userName = node.Select("span.nameBlock span.name").First().InnerText;
                p.image = node.Select("a[name=expandfunc]").First().AbsUrl(baseUrl, "href");
                p.created = long.Parse(node.Select("span.dateTime.postNum").First().Attr("data-utc")) * 1000L;

                callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
            }
        }

#else

        // Версия через доступ к мобильному сайту
		public void ExtractTagPostCollection(ID.TagType type, string tag, int currentPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
			var baseUrl = new Uri(string.Format("http://m2-ch.ru/{0}/{1}", Uri.EscapeDataString(tag), currentPage < 1 ? "" : currentPage + ".html"));
            var doc = downloader.GetDocument(baseUrl).Document.DocumentNode;

            callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });
			callback(new CollectionExportState { 
				State = CollectionExportState.ExportState.TagInfo, 
				TagInfo = new ExportTag { nextPage = currentPage + 1 } });

            foreach (var node in doc.Select("div.thread.hand"))
            {
                var p = new ExportPost();

                p.id = tag + "," + Regex.Match(node.Select("a.bg").First().Attr("href"), "\\d+").Value;

                p.title = node.Select("a.oz").First().InnerText;
                if (string.IsNullOrEmpty(p.title)) p.title = null;

                p.userName = "Unknown"; // TODO
                p.created = new DateTime(2000, 1, 1).ToUnixTimestamp() * 1000L; // TODO

                p.image = node.Select("a.il").First().Attr("href");
                p.imageWidth = 64;
                p.imageHeight = node.Select("div.thumb.z").Select(s => Regex.Match(s.Attr("style"), "height: (\\d+)px;")).Select(s => s.Success ? int.Parse(s.Groups[1].Value) : 64).First();

                callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
            }
        }

#endif

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
