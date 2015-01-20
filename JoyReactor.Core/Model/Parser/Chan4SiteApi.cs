using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using HtmlAgilityPack;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Web.Parser.Data;

namespace JoyReactor.Core.Model.Web.Parser
{
	public class Chan4Parser : SiteApi
	{
		IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader> ();
		Uri baseUrl;
		HtmlDocument document;

		#region ISiteParser implementation

		public override ID.SiteParser ParserId {
			get { return ID.SiteParser.Chan4; }
		}

		public override void ExtractTag (string tag, ID.TagType type, int? currentPageId)
		{
			baseUrl = CreatePageUrl (tag, currentPageId);
			var doc = downloader.Get (baseUrl).DocumentNode;

			ExportTagInformation (currentPageId);
			foreach (var node in doc.Select("div.thread"))
				ExportPostFromHtmlNode (tag, node);
		}

		Uri CreatePageUrl (string tag, int? currentPageId)
		{
			var pagePostfix = currentPageId.HasValue && currentPageId > 0
				? "" + (currentPageId + 1) : "";
			var escapedTag = Uri.EscapeDataString (tag);
			return new Uri (string.Format ("https://boards.4chan.org/{0}/{1}", escapedTag, pagePostfix));
		}

		void ExportTagInformation (int? currentPageId)
		{
			OnNewTagInformation (new ExportTagInformation {
				HasNextPage = true, // TODO: добавить логику
				NextPage = (currentPageId ?? 0) + 1
			});
		}

		void ExportPostFromHtmlNode (string tag, HtmlNode node)
		{
			var p = new ExportPost ();
			var links = node.Select ("a.replylink");
			var m = Regex.Match (links.First ().AbsUrl (baseUrl, "href"), "/thread/(\\d+)");
			if (m.Success)
				p.Id = new ThreadId {
					Board = tag,
					Id = m.Groups [1].Value
				}.Pack ();
			else
				throw new InvalidOperationException ("Can't find post id");
			var imgs = node.Select ("a.fileThumb");
			if (imgs.Any ()) {
				p.Image = imgs.First ().AbsUrl (baseUrl, "href");
				var z = Regex.Match (p.Image, "(\\d+)\\.[\\w\\d]+$").Value;
				m = Regex.Match (node.InnerHtml, "(\\d+)x(\\d+)\\)</div><a class=\"fileThumb\" href=\"//[^/]+/[^/]+/" + Regex.Escape (z));
				p.ImageWidth = int.Parse (m.Groups [1].Value);
				p.ImageHeight = int.Parse (m.Groups [2].Value);
			}
			var titles = node.Select ("span.subject").ToList ();
			if (titles.Any ())
				p.Title = titles.First ().InnerText;
			titles = node.Select ("blockquote.postMessage").ToList ();
			if (titles.Any ())
				p.Title = HtmlEntity.DeEntitize (titles.First ().InnerText.ShortString (100));
			if (string.IsNullOrEmpty (p.Title))
				p.Title = null;
			var dates = node.Select ("span.dateTime");
			p.Created = long.Parse (dates.First ().Attr ("data-utc")) * 1000;
			p.UserName = node.Select ("span.nameBlock span.name").First ().InnerText;

			OnNewPost (p);
		}

		public override void ExtractPost (string postId)
		{
			var thread = ThreadId.Unpack (postId);
			baseUrl = CreateThreadUri (thread);
			document = downloader.Get (baseUrl);

			ExportPostInformation (thread);
			ExportComments ();
		}

		#endregion

		void ExportComments ()
		{
			foreach (var commentNode in document.DocumentNode.Select("div.post.reply")) {
				var comment = GetComment (commentNode);
				OnNewComment (comment);
			}
		}

		ExportPostComment GetComment (HtmlNode node)
		{
			var comment = new ExportPostComment ();

			comment.User = GetUser (node);
			comment.Attachments = GetAttachments (node);

			var id = Regex.Match (node.Id, @"p(\d+)").Groups [1].Value;
			comment.Content = document.GetElementbyId ("m" + id).InnerHtml;
			var utc = node.Select ("span.dateTime.postNum").First ().Attr ("data-utc");
			comment.Created = (long.Parse (utc) * 1000L).DateTimeFromUnixTimestampMs ();

			return comment;
		}

		void ExportPostInformation (ThreadId thread)
		{
			var node = document.GetElementbyId ("p" + thread.Id);
			var data = new ExportPostInformation ();
			data.Content = document.GetElementbyId ("m" + thread.Id).InnerHtml;
			var utc = node.Select ("span.dateTime.postNum").First ().Attr ("data-utc");
			data.Created = (long.Parse (utc) * 1000L).DateTimeFromUnixTimestampMs ();
			data.User = GetUser (node);
			data.Attachments = GetAttachments (node);
			OnNewPostInformation (data);
		}

		ExportAttachment[] GetAttachments (HtmlNode postNode)
		{
			var attachments = new List<ExportAttachment> ();
			foreach (var node in postNode.Select("div.file")) {
				var attach = new ExportAttachment ();
				attach.Image = node
					.Select ("a.fileThumb")
					.Select (s => s.AbsUrl (baseUrl, "href"))
					.First ();
				var fileInfo = node.Select ("div.fileText").First ().InnerHtml;
				var matchInfo = Regex.Match (fileInfo, @", (\d+)x(\d+)\)");
				attach.Width = int.Parse (matchInfo.Groups [1].Value);
				attach.Height = int.Parse (matchInfo.Groups [2].Value);
				attach.Url = node.Select ("a.fileThumb").First ().AbsUrl (baseUrl, "href");
				attachments.Add (attach);
			}
			return attachments.ToArray ();
		}

		ExportUser GetUser (HtmlNode node)
		{
			var name = node.Select ("span.name").First ().InnerHtml;
			var id = node.Select ("span.hand").First ().InnerHtml;
			return new ExportUser { Name = name + " (" + id + ")" };
		}

		Uri CreateThreadUri (ThreadId thread)
		{
			var url = string.Format ("https://boards.4chan.org/{0}/thread/{1}", thread.Board, thread.Id);
			return new Uri (url);
		}
	}
}