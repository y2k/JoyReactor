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

                var titles = node.Select("span.subject");
                if (titles.Count() > 0) p.title = titles.First().InnerText;
                if (string.IsNullOrEmpty(p.title)) p.title = null;

                p.userName = node.Select("span.postername").First().InnerText;

                var imgs = node.Select("div.post_thumb > a");
                if (imgs.Count() > 0) p.image = imgs.First().AbsUrl(baseUrl, "href");

                var m = Regex.Match("</span>[\r\n]*([^\r\n]+)[\r\n]*<span class=\"reflink\">", node.InnerText);
                p.created = NodeHelper.DateTimeToUnixTimestamp(DateTime.ParseExact(m.Groups[1].Value, "", CultureInfo.InvariantCulture)) * 1000L;

                callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });

                //p.serverId = tags[0] + "," + node.id();

                //Elements es = node.select("span.subject");
                //if (!es.isEmpty()) p.title = es.first().text();
                //if (TextUtils.isEmpty(p.title)) p.title = null;

                //p.username = node.select("span.postername").first().text();

                //es = node.select("div.post_thumb > a");
                //if (!es.isEmpty()) p.image = es.first().absUrl("href");

                //es = node.select("div.post_thumb > a > img");
                //if (!es.isEmpty()) p.thumbnail = es.first().absUrl("src");

                //Pattern pattern = Pattern.compile("</span>[\r\n]*([^\r\n]+)[\r\n]*<span class=\"reflink\">");
                //Matcher m = pattern.matcher(node.html());
                //if (m.find()) p.updated = new SimpleDateFormat("yy/MM/dd(ddd)HH:mm", Locale.US).parse(m.group(1));
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