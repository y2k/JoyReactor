using System;
using System.Globalization;
using System.Linq;
using System.Text.RegularExpressions;
using HtmlAgilityPack;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Model.Web.Parser.Data;

namespace JoyReactor.Core.Model.Parser
{
    public class Chan2SiteApi : SiteApi
    {
        static readonly Regex IMAGE_SIZE = new Regex(@"(\d+)x(\d+)");

        IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

        public override ID.SiteParser ParserId
        {
            get { return ID.SiteParser.Chan2; }
        }

        public override void ExtractTag(string tag, ID.TagType type, int? currentPageId)
        {
            var pageUri = CreateUriForTagPosts(tag, currentPageId);
            var doc = downloader.Get(pageUri).DocumentNode;

            ExportTagInformation(currentPageId);
            foreach (var node in doc.Select("div.thread.hand"))
                ExportPost(node, tag);
        }

        Uri CreateUriForTagPosts(string tag, int? currentPageId)
        {
            var url = string.Format("http://m2-ch.ru/{0}/{1}",
                          Uri.EscapeDataString(tag),
                          currentPageId.HasValue && currentPageId > 0 ? currentPageId + ".html" : "");
            return new Uri(url);
        }

        void ExportTagInformation(int? currentPageId)
        {
            OnNewTagInformation(new ExportTagInformation
            {
                NextPage = (currentPageId ?? 0) + 1,
                HasNextPage = true, // TODO Добавить логику
            });
        }

        void ExportPost(HtmlNode node, string tag)
        {
            var post = new ExportPost();
            post.Id = tag + "," + Regex.Match(node.Select("a.bg").First().Attr("href"), "\\d+").Value;
            post.Title = node.Select("div.op.small").First().InnerText.ShortString(100);
            post.UserName = "Anon";

            // TODO
            post.UserImage = "http://img0.joyreactor.cc/pics/avatar/tag/22045";
            post.Created = new DateTime(2000, 1, 1).ToUnixTimestamp() * 1000L;

            // TODO
            post.Image = NormilizeImageUrl(node.Select("a.il").First().Attr("href"));
            post.ImageWidth = 64;
            post.ImageHeight = node.Select("div.thumb.z")
                .Select(s => Regex.Match(s.Attr("style"), "height: (\\d+)px;"))
                .Select(s => s.Success ? int.Parse(s.Groups[1].Value) : 64).First();

            OnNewPost(post);
        }

        static string NormilizeImageUrl(string originalUrl)
        {
            return originalUrl.Replace("https://", "http://").Replace("2ch.hk", "m2-ch.ru");
        }

        public override void ExtractPost(string postId)
        {
            var url = new Uri(string.Format("http://m2-ch.ru/{0}/res/{1}.html", postId.Split(',')[0], postId.Split(',')[1]));
            var doc = downloader.Get(url).DocumentNode;

            ExportPostInformation(url, doc);
            ExportComments(url, doc);
        }

        void ExportPostInformation(Uri url, HtmlNode doc)
        {
            var data = new ExportPostInformation();
            var r = doc.Select("div.thread").First();
            data.Attachments = r.Select("a.thrd-thumb").Select(s => new ExportAttachment
            {
                Image = s.AbsUrl(url, "href").Replace("http://", "https://"),
                Width = int.Parse(IMAGE_SIZE.Match(s.InnerHtml).Groups[1].Value),
                Height = int.Parse(IMAGE_SIZE.Match(s.InnerHtml).Groups[2].Value),
            }).ToArray();
            data.Content = r.Select("div.pst").First().InnerHtml.Replace("<br>", Environment.NewLine);
            OnNewPostInformation(data);
        }

        void ExportComments(Uri url, HtmlNode doc)
        {
            var r = doc.Select("div.thread").First();
            var linker = new ChanPostLinker(r.Id);

            foreach (var c in doc.Select("div.reply"))
            {
                var data = new ExportPostComment();
                data.Attachments = c.Select("a.thrd-thumb").Where(s => !s.Attr("href").EndsWith(".webm")) // TODO: вернуть поддержку webm
                    .Select(s => new ExportAttachment
                    {
                        Image = NormilizeImageUrl(s.AbsUrl(url, "href")),
                        Width = int.Parse(IMAGE_SIZE.Match(s.InnerText).Groups[1].Value),
                        Height = int.Parse(IMAGE_SIZE.Match(s.InnerText).Groups[2].Value),
                    }).ToArray();

                // TODO
                data.User = new ExportUser { Name = "Anon", Avatar = "http://img0.joyreactor.cc/pics/avatar/tag/22045" };
                var d = FixMounthNames(c.Select("time").First().InnerText);
                data.Created = DateTime.ParseExact(d, "dd MMM, HH:mm", new CultureInfo("ru"));

                foreach (var p in linker.Export(c.Select("div.pst").FirstOrDefault(), c.Id))
                {
                    data.Id = p.Id;
                    data.ParentIds = p.ParentIds ?? new string[0];
                    data.Content = p.Content;
                    OnNewComment(data);
                }
            }
        }

        static string FixMounthNames(string date)
        {
            // http://in-coding.blogspot.ru/2012/02/php-date.html
            var months = new CultureInfo("ru").DateTimeFormat.AbbreviatedMonthNames;
            return date
                .Replace("Янв", months[0])
                .Replace("Фев", months[1])
                .Replace("Мар", months[2])
                .Replace("Апр", months[3])
                .Replace("Мая", months[4])
                .Replace("Июн", months[5])
                .Replace("Июл", months[6])
                .Replace("Авг", months[7])
                .Replace("Сен", months[8])
                .Replace("Окт", months[9])
                .Replace("Ноя", months[10])
                .Replace("Дек", months[11]);
        }
    }
}