using System;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;
using System.Linq;
using JoyReactor.Core.Model.Parser.Data;
using System.Text.RegularExpressions;
using HtmlAgilityPack;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web.Parser.Data;

namespace JoyReactor.Core.Model.Web.Parser
{
	public class Chan4Parser : SiteParser
	{
		private IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader> ();
		private Uri baseUrl;
		private HtmlDocument document;

		#region ISiteParser implementation

		public override ID.SiteParser ParserId {
			get { return ID.SiteParser.Chan4; }
		}

		public override void ExtractTag (ID.TagType type, string tag, int currentPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
		{
			var pagePostfix = currentPage == 0 ? "" : "" + (currentPage + 1);
			var escapedTag = Uri.EscapeDataString (tag);
			baseUrl = new Uri (string.Format ("https://boards.4chan.org/{0}/{1}", escapedTag, pagePostfix));
			var doc = downloader.Get (baseUrl).DocumentNode;

			callback (new CollectionExportState { State = CollectionExportState.ExportState.Begin });
			callback (new CollectionExportState { 
				State = CollectionExportState.ExportState.TagInfo, 
				TagInfo = new ExportTag { NextPage = currentPage + 1 }
			});

			foreach (var node in doc.Select("div.thread")) {
				var p = new ExportPost ();

				var links = node.Select ("a.replylink");
				var m = Regex.Match (links.First ().AbsUrl (baseUrl, "href"), "/thread/(\\d+)");
				if (m.Success)
					p.Id = new ThreadId { Board = tag, Id = m.Groups [1].Value }.Pack ();
				else
					throw new InvalidOperationException ("Can't find post id");

				var imgs = node.Select ("a.fileThumb");
				if (imgs.Count () > 0) {
					p.Image = imgs.First ().AbsUrl (baseUrl, "href");

					var z = Regex.Match (p.Image, "(\\d+)\\.[\\w\\d]+$").Value;
					m = Regex.Match (node.InnerHtml, "(\\d+)x(\\d+)\\)</div><a class=\"fileThumb\" href=\"//[^/]+/[^/]+/" + Regex.Escape (z));
					p.ImageWidth = int.Parse (m.Groups [1].Value);
					p.ImageHeight = int.Parse (m.Groups [2].Value);
				}

				var titles = node.Select ("span.subject");
				if (titles.Count () > 0)
					p.Title = titles.First ().InnerText;
				titles = node.Select ("blockquote.postMessage");
				if (titles.Count () > 0)
					p.Title = HtmlEntity.DeEntitize (titles.First ().InnerText.ShortString (100));
				if (string.IsNullOrEmpty (p.Title))
					p.Title = null;

				var dates = node.Select ("span.dateTime");
				p.Created = long.Parse (dates.First ().Attr ("data-utc")) * 1000;
				p.UserName = node.Select ("span.nameBlock span.name").First ().InnerText;

				callback (new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });
			}
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

		private void ExportComments ()
		{
			foreach (var commentNode in document.DocumentNode.Select("div.post.reply")) {
				var comment = GetComment (commentNode);
				OnNewComment (comment);
			}
		}

		private ExportPostComment GetComment (HtmlNode node)
		{
			var comment = new ExportPostComment ();

			comment.User = GetUser (node);
			comment.Attachments = GetAttachments (node);

			var id = Regex.Match (node.Id, @"p(\d+)").Groups [1].Value;
			comment.Content = document.GetElementbyId ("m" + id).InnerHtml;
			var utc = node.Select ("span.dateTime.postNum").First ().Attr ("data-utc");
			comment.Created = (long.Parse (utc) * 1000L).DateTimeFromUnixTimestamp ();

			return comment;
		}

		private void ExportPostInformation (ThreadId thread)
		{
			var node = document.GetElementbyId ("p" + thread.Id);
			var data = new ExportPostInformation ();
			data.Content = document.GetElementbyId ("m" + thread.Id).InnerHtml;
			var utc = node.Select ("span.dateTime.postNum").First ().Attr ("data-utc");
			data.Created = (long.Parse (utc) * 1000L).DateTimeFromUnixTimestamp ();
			data.User = GetUser (node);
			data.Attachments = GetAttachments (node);
			OnNewPostInformation (data);
		}

		private ExportAttachment[] GetAttachments (HtmlNode postNode)
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

		private ExportUser GetUser (HtmlNode node)
		{
			var name = node.Select ("span.name").First ().InnerHtml;
			var id = node.Select ("span.hand").First ().InnerHtml;
			return new ExportUser { Name = name + " (" + id + ")" };
		}

		private Uri CreateThreadUri (ThreadId thread)
		{
			var url = string.Format ("https://boards.4chan.org/{0}/thread/{1}", thread.Board, thread.Id);
			return new Uri (url);
		}
	}
}