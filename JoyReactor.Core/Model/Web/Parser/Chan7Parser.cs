using HtmlAgilityPack;
using JoyReactor.Core.Model.Helper;
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
    public class Chan7Parser : SiteParser
    {
        private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

		public override ID.SiteParser ParserId
        {
            get { return ID.SiteParser.Chan7; }
        }

		public override IDictionary<string, string> Login(string username, string password)
        {
            throw new NotImplementedException();
        }

		public override void ExtractTagPostCollection(ID.TagType type, string tag, int currentPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
			var baseUrl = new Uri(string.Format("https://7chan.org/{0}/{1}", 
				Uri.EscapeDataString(tag), 
				currentPage < 1 ? "" : currentPage + ".html"));
            var doc = downloader.Get(baseUrl).DocumentNode;

            callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });
			callback(new CollectionExportState { 
				State = CollectionExportState.ExportState.TagInfo, 
				TagInfo = new ExportTag { NextPage = currentPage + 1 } });

            foreach (var node in doc.Select("div.op > div"))
            {
                var p = new ExportPost();

                p.Id = tag.Trim().ToLower() + "," + node.Id;
                p.UserName = node.Select("span.postername").First().InnerText;

                var titles = node.Select("span.subject");
                if (titles.Count() > 0) p.Title = titles.First().InnerText;
                titles = node.Select("p.message");
                if (titles.Count() > 0) p.Title = HtmlEntity.DeEntitize(titles.First().InnerText.ShortString(100).Trim('\r', '\n', ' '));
                if (string.IsNullOrEmpty(p.Title)) p.Title = null;

                var m = Regex.Match(node.InnerHtml, "<a href=\"([^\"]+)\" id=\"expandimg_" + node.Id + "_(\\d+)_(\\d+)");
                if (m.Success)
                {
                    p.Image = m.Groups[1].Value.Replace("https://", "http://");
                    p.ImageWidth = int.Parse(m.Groups[2].Value);
                    p.ImageHeight = int.Parse(m.Groups[3].Value);
                }

                m = Regex.Match(node.InnerHtml, "</span>[\r\n]*([^\r\n]+)[\r\n]*<span class=\"reflink\">");
                if (!m.Success) throw new InvalidOperationException("Can't find date-time in post " + p.Id);
				p.Created = DateTime
					.ParseExact(m.Groups[1].Value, "yy/MM/dd(ddd)HH:mm", CultureInfo.InvariantCulture)
					.ToUnixTimestamp();

                callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
            }
        }
    }
}