using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Text.RegularExpressions;
using HtmlAgilityPack;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Web.Parser.Data;

namespace JoyReactor.Core.Model.Web.Parser
{
	public class Chan7Parser : SiteParser
	{
		/// <summary>
		/// 14/05/06(Tue)10:40
		/// </summary>
		private static readonly Regex DateRegex = new Regex (@"(\d{2})/(\d{2})/(\d{2})\([^\)]+\)(\d{2}):(\d{2})");

		private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader> ();

		private HtmlDocument document;
		private Uri pageUrl;
		private ThreadId threadId;

		public override ID.SiteParser ParserId {
			get { return ID.SiteParser.Chan7; }
		}

		public override void ExtractPost (string postId)
		{
			threadId = ThreadId.Unpack (postId);
			pageUrl = GenerateThreadUrl (threadId);
			document = downloader.Get (pageUrl);

			var postInfo = GetPostInformation ();
			OnNewPost (postInfo);
		}

		private Uri GenerateThreadUrl (ThreadId id)
		{
			return new Uri (string.Format ("https://7chan.org/{0}/res/{1}.html", id.Board, id.Id));
		}

		private ExportPostInformation GetPostInformation ()
		{
			var postInfo = new ExportPostInformation ();
			var node = document.GetElementById ("" + threadId.Id);

			postInfo.User = GetUserFromHtmlNode (node);
			postInfo.Content = GetPostTextContent (node);
			postInfo.Created = GetPostCreated (node);
			postInfo.Attachments = GetAttachments (node);

			return postInfo;
		}

		private ExportUser GetUserFromHtmlNode (HtmlNode node)
		{
			return new ExportUser { 
				Name = node.Select ("div.postername").First ().InnerText
			};
		}

		private string GetPostTextContent (HtmlNode node)
		{
			var content = node
				.Select ("p.message").First ()
				.InnerText.Replace ("<br />", "");
			content = WebUtility.HtmlDecode (content);
			return content;
		}

		private DateTime GetPostCreated (HtmlNode node)
		{
			var dateMatch = DateRegex.Match (node.InnerText);
			if (!dateMatch.Success)
				throw new Exception ("Date not found in:\n" + node.InnerText);

			var year = 2000 + int.Parse (dateMatch.Groups [1].Value);
			var month = int.Parse (dateMatch.Groups [2].Value);
			var day = int.Parse (dateMatch.Groups [3].Value);
			var hour = int.Parse (dateMatch.Groups [4].Value);
			var minute = int.Parse (dateMatch.Groups [5].Value);
			return new DateTime (year, month, day, hour, minute, 0);
		}

		private ExportAttachment[] GetAttachments (HtmlNode node)
		{
			var attachments = new List<ExportAttachment> ();
			if (node.Select ("p.file_size").Any ()) {
				//
				attachments.Add (GetSingleAttachment (node));
			} else {
				//
				attachments.AddRange (GetMultiAttachments (node));
			}
			return attachments.ToArray ();
		}

		private IEnumerable<ExportAttachment> GetMultiAttachments (HtmlNode node)
		{
			throw new NotImplementedException ();
		}

		private ExportAttachment GetSingleAttachment (HtmlNode node)
		{
			var attachment = new ExportAttachment ();

			var p = node.Select ("p.file_size").First ();

			return attachment;
		}

		public override void ExtractTagPostCollection (ID.TagType type, string tag, int currentPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
		{
			var baseUrl = new Uri (string.Format ("https://7chan.org/{0}/{1}", 
				              Uri.EscapeDataString (tag), 
				              currentPage < 1 ? "" : currentPage + ".html"));
			var doc = downloader.Get (baseUrl).DocumentNode;

			callback (new CollectionExportState { State = CollectionExportState.ExportState.Begin });
			callback (new CollectionExportState { 
				State = CollectionExportState.ExportState.TagInfo, 
				TagInfo = new ExportTag { NextPage = currentPage + 1 }
			});

			foreach (var node in doc.Select("div.op > div")) {
				var p = new ExportPost ();

				p.Id = tag.Trim ().ToLower () + "," + node.Id;
				p.UserName = node.Select ("span.postername").First ().InnerText;

				var titles = node.Select ("span.subject");
				if (titles.Count () > 0)
					p.Title = titles.First ().InnerText;
				titles = node.Select ("p.message");
				if (titles.Count () > 0)
					p.Title = HtmlEntity.DeEntitize (titles.First ().InnerText.ShortString (100).Trim ('\r', '\n', ' '));
				if (string.IsNullOrEmpty (p.Title))
					p.Title = null;

				var m = Regex.Match (node.InnerHtml, "<a href=\"([^\"]+)\" id=\"expandimg_" + node.Id + "_(\\d+)_(\\d+)");
				if (m.Success) {
					p.Image = m.Groups [1].Value.Replace ("https://", "http://");
					p.ImageWidth = int.Parse (m.Groups [2].Value);
					p.ImageHeight = int.Parse (m.Groups [3].Value);
				}

				m = Regex.Match (node.InnerHtml, "</span>[\r\n]*([^\r\n]+)[\r\n]*<span class=\"reflink\">");
				if (!m.Success)
					throw new InvalidOperationException ("Can't find date-time in post " + p.Id);
				p.Created = DateTime
					.ParseExact (m.Groups [1].Value, "yy/MM/dd(ddd)HH:mm", CultureInfo.InvariantCulture)
					.ToUnixTimestamp ();

				callback (new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
			}
		}
	}
}