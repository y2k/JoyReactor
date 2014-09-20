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
using System.Globalization;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Core.Model.Web.Parser
{
    public class Chan2Parser : ISiteParser
    {
		private static readonly Regex IMAGE_SIZE = new Regex (@"(\d+)x(\d+)");

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

				p.image = node.Select("a.il").First().Attr("href").Replace("http://", "https://");
                p.imageWidth = 64;
                p.imageHeight = node.Select("div.thumb.z").Select(s => Regex.Match(s.Attr("style"), "height: (\\d+)px;")).Select(s => s.Success ? int.Parse(s.Groups[1].Value) : 64).First();

                callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
            }
        }

#endif

        public void ExtractPost(string postId, Action<PostExportState> callback)
        {
			var url = new Uri (string.Format ("http://m2-ch.ru/{0}/res/{1}.html", postId.Split(',')[0], postId.Split(',')[1]));
			var doc = downloader.Get(url).DocumentNode;

			callback(new PostExportState { State = PostExportState.ExportState.Begin });

			var state = new PostExportState { State = PostExportState.ExportState.Info };
			var r = doc.Select ("div.thread").First ();
			state.Attachments = r.Select ("a.thrd-thumb")
				.Select (s => new ExportPostAttachment {
					Image = s.AbsUrl(url, "href").Replace("http://", "https://"),
					Width = int.Parse(IMAGE_SIZE.Match(s.InnerHtml).Groups[1].Value),
					Height = int.Parse(IMAGE_SIZE.Match(s.InnerHtml).Groups[2].Value),
				}).ToArray();
			state.Content = r.Select ("div.pst").First ().InnerHtml;
			callback (state);

			state.State = PostExportState.ExportState.Comment;
			state.Comment = new ExportComment ();
			var subIds = new Dictionary<string, int> ();
			var clist = doc.Select ("div.reply");
			foreach (var c in clist) {
				state.Comment.Attachments = c.Select ("a.thrd-thumb")
					.Where(s => !s.Attr("href").EndsWith(".webm")) // TODO: вернуть поддержку webm
					.Select (s => new ExportComment.ExportAttachment {
						Image = s.AbsUrl(url, "href").Replace("http://", "https://"),
						Width = int.Parse(IMAGE_SIZE.Match(s.InnerText).Groups[1].Value),
						Height = int.Parse(IMAGE_SIZE.Match(s.InnerText).Groups[2].Value),
					}).ToArray();

				var d = FixMounthNames(c.Select ("time").First ().InnerText);
				state.Comment.Created = DateTime.ParseExact (d, "dd MMM, HH:mm", new CultureInfo ("ru")).ToUnixTimestamp();

				var ps = new List<string> ();
				HtmlDocument pc = null;
				int index = 0;
				foreach (var z in r.Select ("div.pst").First ().ChildNodes) {
					if (z.Name == "a" && z.InnerHtml.StartsWith ("&gt;&gt;")) {
						if (pc == null) {
							var id = Regex.Match (z.InnerHtml, "&gt;&gt;(\\d+)").Groups [1].Value;
							int cnt = 1;
							subIds.TryGetValue (id, out cnt);
							for (int i = 0; i < cnt; i++) {
								ps.Add (id + "-" + i);
							}
						} else {
							if (ps.Count == 0 || ps.All(s => s == r.Id + "-0")) {
								state.Comment.id = c.Id + "-0";
								state.Comment.ParentIds = null;
								state.Comment.text = pc.DocumentNode.InnerHtml;
								callback (state);
							} else {
								state.Comment.id = c.Id + "-" + (index++);
								state.Comment.ParentIds = ps.ToArray ();
								state.Comment.text = pc.DocumentNode.InnerHtml;
								callback (state);
							}
							ps.Clear ();
							pc = null;
						}
					} else {
						pc = pc ?? new HtmlDocument ();
						pc.DocumentNode.ChildNodes.Add (z);
					}
				}

				if (index > 1)
					subIds [c.Id] = index;
			}
        }

        public ProfileExport Profile(string username)
        {
            throw new NotImplementedException();
        }

		private static string FixMounthNames(string date) {
			// http://in-coding.blogspot.ru/2012/02/php-date.html
			return date
				.Replace ("Янв", "Янв.")
				.Replace ("Фев", "Февр.")
				.Replace ("Мар", "Март")
				.Replace ("Апр", "Апр.")
				.Replace ("Мая", "Май")
				.Replace ("Июн", "Июнь")
				.Replace ("Июл", "Июль")
				.Replace ("Авг", "Авг.")
				.Replace ("Сен", "Сент.")
				.Replace ("Окт", "Окт.")
				.Replace ("Ноя", "Нояб.")
				.Replace ("Дек", "Дек.");
		}
    }
}