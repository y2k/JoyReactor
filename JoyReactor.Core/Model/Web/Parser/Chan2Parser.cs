using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using HtmlAgilityPack;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Web.Parser.Data;

namespace JoyReactor.Core.Model.Web.Parser
{
	public class Chan2Parser : SiteParser
	{
		private static readonly Regex IMAGE_SIZE = new Regex (@"(\d+)x(\d+)");

		private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader> ();

		public override ID.SiteParser ParserId {
			get { return ID.SiteParser.Chan2; }
		}

		// Версия через доступ к мобильному сайту
		public override void ExtractTagPostCollection (ID.TagType type, string tag, int currentPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
		{
			var baseUrl = new Uri (string.Format ("http://m2-ch.ru/{0}/{1}", Uri.EscapeDataString (tag), currentPage < 1 ? "" : currentPage + ".html"));
			var doc = downloader.Get (baseUrl).DocumentNode;

			callback (new CollectionExportState { State = CollectionExportState.ExportState.Begin });
			callback (new CollectionExportState { 
				State = CollectionExportState.ExportState.TagInfo, 
				TagInfo = new ExportTag { NextPage = currentPage + 1 }
			});

			foreach (var node in doc.Select("div.thread.hand")) {
				var p = new ExportPost ();

				p.Id = tag + "," + Regex.Match (node.Select ("a.bg").First ().Attr ("href"), "\\d+").Value;

//				p.Title = node.Select ("a.oz").First ().InnerText;
//				if (string.IsNullOrEmpty (p.Title))
//					p.Title = null;
				p.Title = node.Select ("div.op.small").First ().InnerText.ShortString (100);

				p.UserName = "Anon"; // TODO
				p.UserImage = "http://img0.joyreactor.cc/pics/avatar/tag/22045";
				p.Created = new DateTime (2000, 1, 1).ToUnixTimestamp () * 1000L; // TODO

				p.Image = node.Select ("a.il").First ().Attr ("href").Replace ("http://", "https://");
				p.ImageWidth = 64;
				p.ImageHeight = node.Select ("div.thumb.z").Select (s => Regex.Match (s.Attr ("style"), "height: (\\d+)px;")).Select (s => s.Success ? int.Parse (s.Groups [1].Value) : 64).First ();

				callback (new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
			}
		}

		public override void ExtractPost (string postId)
		{
			var url = new Uri (string.Format ("http://m2-ch.ru/{0}/res/{1}.html", postId.Split (',') [0], postId.Split (',') [1]));
			var doc = downloader.Get (url).DocumentNode;

			ExportPostInformation (url, doc);
			ExportComments (url, doc);
		}

		private void ExportPostInformation (Uri url, HtmlNode doc)
		{
			var data = new ExportPostInformation ();
			var r = doc.Select ("div.thread").First ();
			data.Attachments = r.Select ("a.thrd-thumb").Select (s => new ExportAttachment {
				Image = s.AbsUrl (url, "href").Replace ("http://", "https://"),
				Width = int.Parse (IMAGE_SIZE.Match (s.InnerHtml).Groups [1].Value),
				Height = int.Parse (IMAGE_SIZE.Match (s.InnerHtml).Groups [2].Value),
			}).ToArray ();
			data.Content = r.Select ("div.pst").First ().InnerHtml.Replace ("<br>", Environment.NewLine);
			OnNewPost (data);
		}

		private void ExportComments (Uri url, HtmlNode doc)
		{
			var r = doc.Select ("div.thread").First ();
			var subIds = new Dictionary<string, int> ();

			foreach (var c in doc.Select ("div.reply")) {
				var data = new ExportPostComment ();
				data.Attachments = c.Select ("a.thrd-thumb").Where (s => !s.Attr ("href").EndsWith (".webm"))// TODO: вернуть поддержку webm
					.Select (s => new ExportAttachment {
					Image = s.AbsUrl (url, "href").Replace ("http://", "https://"),
					Width = int.Parse (IMAGE_SIZE.Match (s.InnerText).Groups [1].Value),
					Height = int.Parse (IMAGE_SIZE.Match (s.InnerText).Groups [2].Value),
				}).ToArray ();
				// TODO
				data.User = new ExportUser { Name = "Anon", Avatar = "http://img0.joyreactor.cc/pics/avatar/tag/22045" };
				var d = FixMounthNames (c.Select ("time").First ().InnerText);
				data.Created = DateTime.ParseExact (d, "dd MMM, HH:mm", new CultureInfo ("ru"));

				var ps = new List<string> ();
				HtmlDocument pc = null;
				int index = 0;

				foreach (var z in c.Select ("div.pst").SelectMany (s => s.ChildNodes)) {
					if (z.Name == "a" && z.InnerHtml.StartsWith (">>")) {
						if (pc == null) {
							var id = Regex.Match (z.InnerHtml, ">>(\\d+)").Groups [1].Value;
							int cnt;
							if (!subIds.TryGetValue (id, out cnt))
								cnt = 1;
							for (int i = 0; i < cnt; i++) {
								ps.Add (id + "-" + i);
							}
						} else {
							if (ps.Count == 0 || ps.All (s => s == r.Id + "-0")) {
								data.Id = c.Id + "-0";
								data.ParentIds = null;
								data.Content = pc.DocumentNode.InnerHtml;
							} else {
								data.Id = c.Id + "-" + (index++);
								data.ParentIds = ps.ToArray ();
								data.Content = pc.DocumentNode.InnerHtml;
							}
							OnNewComment (data);
							ps.Clear ();
							pc = null;
						}
					} else {
						pc = pc ?? new HtmlDocument ();
						pc.DocumentNode.AppendChild (z);
					}
				}
				if (pc != null) {
					if (ps.Count == 0 || ps.All (s => s == r.Id + "-0")) {
						data.Id = c.Id + "-0";
						data.ParentIds = null;
						data.Content = pc.DocumentNode.InnerHtml;
					} else {
						data.Id = c.Id + "-" + (index++);
						data.ParentIds = ps.ToArray ();
						data.Content = pc.DocumentNode.InnerHtml;
					}
					OnNewComment (data);
				}
				if (index > 1)
					subIds [c.Id] = index;
			}
		}

		private static string FixMounthNames (string date)
		{
			// http://in-coding.blogspot.ru/2012/02/php-date.html
			var months = new CultureInfo ("ru").DateTimeFormat.AbbreviatedMonthNames;
			return date
				.Replace ("Янв", months [0])
				.Replace ("Фев", months [1])
				.Replace ("Мар", months [2])
				.Replace ("Апр", months [3])
				.Replace ("Мая", months [4])
				.Replace ("Июн", months [5])
				.Replace ("Июл", months [6])
				.Replace ("Авг", months [7])
				.Replace ("Сен", months [8])
				.Replace ("Окт", months [9])
				.Replace ("Ноя", months [10])
				.Replace ("Дек", months [11]);
		}
	}
}