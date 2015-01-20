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
	public class Chan7Parser : SiteApi
	{
		/// <summary>
		/// 14/05/06(Tue)10:40
		/// </summary>
		static readonly Regex DateRegex = new Regex (@"(\d{2})/(\d{2})/(\d{2})\([^\)]+\)(\d{2}):(\d{2})");
		static readonly Regex AttachmentRegex = 
			new Regex ("a href=\"([^\"]*)\" id=\"expandimg_[\\d-]+_(\\d+)_(\\d+)_\\d+_\\d+\">");

		IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader> ();

		HtmlDocument document;
		Uri pageUrl;
		ThreadId threadId;

		public override ID.SiteParser ParserId {
			get { return ID.SiteParser.Chan7; }
		}

		public override void ExtractPost (string postId)
		{
			threadId = ThreadId.Unpack (postId);
			pageUrl = GenerateThreadUrl (threadId);
			document = downloader.Get (pageUrl);

			var postInfo = GetPostInformation ();
			OnNewPostInformation (postInfo);

			ExportComments ();
		}

		Uri GenerateThreadUrl (ThreadId id)
		{
			return new Uri (string.Format ("https://7chan.org/{0}/res/{1}.html", id.Board, id.Id));
		}

		ExportPostInformation GetPostInformation ()
		{
			var postInfo = new ExportPostInformation ();
			var node = document.GetElementbyId ("" + threadId.Id);
			postInfo.User = GetUserFromHtmlNode (node);
			postInfo.Content = GetPostTextContent (node);
			postInfo.Created = GetPostCreated (node);
			postInfo.Attachments = GetAttachments (node);
			return postInfo;
		}

		ExportUser GetUserFromHtmlNode (HtmlNode node)
		{
			return new ExportUser { 
				Name = node.Select ("span.postername").First ().InnerText
			};
		}

		string GetPostTextContent (HtmlNode node)
		{
			var content = node
				.Select ("p.message").First ()
				.InnerText.Replace ("<br />", "").Trim (' ', '\n');
			content = WebUtility.HtmlDecode (content);
			return content;
		}

		DateTime GetPostCreated (HtmlNode node)
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

		ExportAttachment[] GetAttachments (HtmlNode node)
		{
			return AttachmentRegex
				.Matches (node.InnerHtml)
				.Cast<Match> ()
				.Select (
				m => {
					var attachment = new ExportAttachment ();
					attachment.Image = m.Groups [1].Value;
					attachment.Width = int.Parse (m.Groups [2].Value);
					attachment.Height = int.Parse (m.Groups [3].Value);
					return attachment;
				})
				.ToArray ();
		}

		void ExportComments ()
		{
			foreach (var node in document.DocumentNode.Select ("div.reply div.post")) {
				var comment = GetComment (node);
				OnNewComment (comment);
			}
		}

		ExportPostComment GetComment (HtmlNode node)
		{
			var comment = new ExportPostComment ();
			comment.Attachments = GetAttachments (node);
			comment.Content = GetPostTextContent (node);
			comment.Created = GetPostCreated (node);
			comment.User = GetUserFromHtmlNode (node);
			comment.Id = node.Id;
			return comment;
		}

		public override void ExtractTag (string tag, ID.TagType type, int? currentPageId)
		{
			var baseUrl = CreatePageUri (tag, currentPageId);
			var doc = downloader.Get (baseUrl).DocumentNode;

			ExportTagInformation (currentPageId);
			foreach (var node in doc.Select("div.op > div"))
				ExportTagFromHtmlNode (tag, node);
		}

		Uri CreatePageUri (string tag, int? currentPageId)
		{
			var page = !currentPageId.HasValue || currentPageId < 1 ? "" : currentPageId + ".html";
			return new Uri (string.Format ("https://7chan.org/{0}/{1}", Uri.EscapeDataString (tag), page));
		}

		void ExportTagInformation (int? currentPageId)
		{
			OnNewTagInformation (new ExportTagInformation {
				NextPage = (currentPageId ?? 0) + 1,
				HasNextPage = true, // TODO:
			});
		}

		void ExportTagFromHtmlNode (string tag, HtmlNode node)
		{
			var p = new ExportPost ();
			p.Id = tag.Trim ().ToLower () + "," + node.Id;
			p.UserName = node.Select ("span.postername").First ().InnerText;
			var titles = node.Select ("span.subject").ToList ();
			if (titles.Any ())
				p.Title = titles.First ().InnerText;
			titles = node.Select ("p.message").ToList ();
			if (titles.Any ())
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
			p.Created = DateTime.ParseExact (m.Groups [1].Value, "yy/MM/dd(ddd)HH:mm", CultureInfo.InvariantCulture).ToUnixTimestamp ();

			OnNewPost (p);
		}
	}
}