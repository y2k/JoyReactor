using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using HtmlAgilityPack;
using System.Linq;

namespace JoyReactor.Core.Model.Parser
{
    class TagProvider
    {
        JoyReactorProvider.IAuthStorage authStorage = ServiceLocator.Current.GetInstance<JoyReactorProvider.IAuthStorage>();
        IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
        JoyReactorProvider.IStorage storage = ServiceLocator.Current.GetInstance<JoyReactorProvider.IStorage>();

        JoyReactorProvider.IListStorage listStorage;
        ID id;
        string pageHtml;

        public TagProvider(ID id, JoyReactorProvider.IListStorage listStorage)
        {
            this.id = id;
            this.listStorage = listStorage;
        }

        internal async Task ComputeAsync()
        {
            pageHtml = await DownloadTagPageWithCheckDomainAsync();
            await ExtractTagInformationAsync();
            await ExtractPostsAsync();
            await ExtractLinkedTags();
        }

        #region Download page

        async Task<string> DownloadTagPageWithCheckDomainAsync()
        {
            var html = await DownloadTagPageAsync();
            if (IsPageFromSecretSite(html))
            {
                new ReactorDomainDetector().SetTagType(id.Tag, ReactorDomainDetector.TagType.Secret);
                return await DownloadTagPageAsync();
            }
            return html;
        }

        async Task<string> DownloadTagPageAsync()
        {
            return await downloader.GetTextAsync(
                await GenerateUrl(),
                new RequestParams
                {
                    Cookies = await GetCookiesAsync(),
                    UseForeignProxy = true,
                });
        }

        async Task<Uri> GenerateUrl()
        {
            var url = new StringBuilder("http://" + new ReactorDomainDetector().GetDomainForTag(id.Tag));
            if (id.Type == ID.TagType.Favorite)
            {
                // TODO: перевести на асинхронную модель
                if (id.Tag == null)
                    id.Tag = await authStorage.GetCurrentUserNameAsync();
                url.Append("/user/").Append(Uri.EscapeDataString(id.Tag)).Append("/favorite");
            }
            else
            {
                if (id.Tag != null)
                    url.Append("/tag/").Append(Uri.EscapeUriString(id.Tag));
                if (ID.TagType.Best == id.Type)
                    url.Append("/best");
                else if (ID.TagType.All == id.Type)
                    url.Append(id.Tag == null ? "/all" : "/new");
            }

            int currentPage = await storage.GetNextPageForTagAsync(id);
            if (currentPage > 0)
                url.Append("/").Append(currentPage);
            return new Uri("" + url);
        }

        bool IsPageFromSecretSite(string html)
        {
            return html.Contains(">секретные разделы</a>");
        }

        async Task<IDictionary<string, string>> GetCookiesAsync()
        {
            var cookies = await authStorage.GetCookiesAsync();
            cookies.Add("showVideoGif2", "1");
            return cookies;
        }

        #endregion

        #region Extract tag information

        Task ExtractTagInformationAsync()
        {
            var imageRx = new Regex("src=\"([^\"]+)\" *alt=\"[^\"]+\" *class=\"blog_avatar\" */>");
            var image = imageRx.FirstString(pageHtml);
            var nextPage = GetNextPageOfTagList();
            var hasNextPage = GetNextPageOfTagList() > 0;
            return storage.UpdateTagInformationAsync(id, image, nextPage, hasNextPage);
        }

        int GetNextPageOfTagList()
        {
            var currentPageRx = new Regex("<span class='current'>(\\d+)</span>");
            return currentPageRx.FirstInt(pageHtml) - 1;
        }

        #endregion

        #region Extract posts

        async Task ExtractPostsAsync()
        {
            foreach (var htmlPost in GetPostHtmlList())
                await SavePostAsync(htmlPost);
            await listStorage.CommitAsync();
        }

        IEnumerable<string> GetPostHtmlList()
        {
            var postRx = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus unregistered\">", RegexOptions.Singleline);
            var m = postRx.Match(pageHtml);
            if (m.Success)
            {
                do
                {
                    yield return m.Groups[1].Value;
                    m = m.NextMatch();
                } while (m.Success);
            }
            else
            {
                var AuthPostRx = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus", RegexOptions.Singleline);
                m = AuthPostRx.Match(pageHtml);
                while (m.Success)
                {
                    yield return m.Groups[1].Value;
                    m = m.NextMatch();
                }
            }
        }

        async Task SavePostAsync(string html)
        {
            var p = new Post();

            var ImageRx = new Regex("<div class=\"image\">\\s*<img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", RegexOptions.Singleline);
            var m = ImageRx.Match(html);
            if (m.Success)
            {
                p.Image = m.Groups[1].Value;
                p.ImageWidth = int.Parse(m.Groups[2].Value);
                p.ImageHeight = int.Parse(m.Groups[3].Value);
            }
            if (p.Image == null)
            {
                var GifImageRx = new Regex("ссылка на гифку</a><img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)");
                m = GifImageRx.Match(html);
                if (m.Success)
                {
                    p.Image = m.Groups[1].Value;
                    p.ImageWidth = int.Parse(m.Groups[2].Value);
                    p.ImageHeight = int.Parse(m.Groups[3].Value);
                }
            }
            if (p.Image == null)
            {
                var BigImageRx = new Regex("<div class=\"image\">\\s*<a href=\"([^\"]+)\" class=\"prettyPhotoLink\" rel=\"prettyPhoto\">\\s*<img src=\"[^\"]+\" width=\"(\\d+)\" height=\"(\\d+)\"", RegexOptions.Singleline);
                m = BigImageRx.Match(html);
                if (m.Success)
                {
                    p.Image = m.Groups[1].Value;
                    p.ImageWidth = int.Parse(m.Groups[2].Value);
                    p.ImageHeight = int.Parse(m.Groups[3].Value);
                }
            }
            if (p.Image == null)
            {
                var ImageFromSharing = new Regex("\\[img\\]([^\\[]+)\\[/img\\]");
                p.Image = ImageFromSharing.FirstString(html);
                if (p.Image != null)
                { // XXX Проверить нужно ли ставить фейковые размеры картинки и какое конкретно число
                    p.ImageWidth = 512;
                    p.ImageHeight = 512;
                }
            }
            if (p.Image != null)
            {
                p.Image = Regex.Replace(p.Image, "/pics/post/full/[\\w\\s%-]+-(\\d+\\.[\\d\\w]+)", "/pics/post/full/-$1");
                p.Image = Regex.Replace(p.Image, "/pics/post/[\\w\\s%-]+-(\\d+\\.[\\d\\w]+)", "/pics/post/full/-$1");
            }

            var UserNameRx = new Regex("href=\"[^\"]+user/([^\"/]+)\"", RegexOptions.Singleline);
            p.UserName = Uri.UnescapeDataString(Uri.UnescapeDataString(UserNameRx.FirstString(html))).Replace('+', ' ');
            var UserImageRx = new Regex("src=\"([^\"]+)\" class=\"avatar\"");
            p.UserImage = UserImageRx.FirstString(html);

            var TitleRx = new Regex("<div class=\"post_content\"><span>([^<]*)</span>", RegexOptions.Singleline);
            p.Title = TitleRx.FirstString(html);
            if (string.IsNullOrEmpty(p.Title))
                p.Title = null;

            var PostIdRx = new Regex("<a href=\"/post/(\\d+)\"", RegexOptions.Singleline);
            p.PostId = PostIdRx.FirstString(html);
            var CreatedRx = new Regex("data\\-time=\"(\\d+)\"");
            p.Created = CreatedRx.FirstLong(html) * 1000L;
            var RatingRx = new Regex("Рейтинг:\\s*<div class=\"[^\"]+\"></div>\\s*([\\d\\.]+)");
            p.Rating = RatingRx.FirstFloat(html, CultureInfo.InvariantCulture);

            var CoubRx = new Regex("<iframe src=\"http://coub.com/embed/(.+?)\" allowfullscreen=\"true\" frameborder=\"0\" width=\"(\\d+)\" height=\"(\\d+)");
            m = CoubRx.Match(html);
            if (m.Success)
            {
                p.Coub = m.Groups[1].Value;
                p.ImageWidth = int.Parse(m.Groups[2].Value);
                p.ImageHeight = int.Parse(m.Groups[2].Value);
            }

            await storage.SaveNewOrUpdatePostAsync(p);
            listStorage.AddPost(p);
        }

        #endregion

        #region Extract linked tags

        Task ExtractLinkedTags()
        {
            return Task.Run(async () =>
                {
                    var doc = new HtmlDocument();
                    doc.LoadHtml(pageHtml);

                    await storage.RemoveLinkedTagAsync(id);
                    foreach (var s in doc.DocumentNode.Select("div.sidebar_block"))
                        await ExtractTagsFromBlock(s);
                });
        }

        async Task ExtractTagsFromBlock(HtmlNode blockNode)
        {
            var title = blockNode.Select("h2.sideheader.random").FirstOrDefault()?.InnerText.Trim();
            var tags = LinkedTagExtractor.Get().Select(s => s.Extract(blockNode)).Where(s => s.Count > 0).FirstOrDefault();
            if (title != null && tags != null)
                await storage.SaveLinkedTagsAsync(id, title, tags);
        }

        abstract class LinkedTagExtractor {

            internal static ICollection<LinkedTagExtractor> Get() {
                return new LinkedTagExtractor[] { new RandomTagExtractor(), new SubTagExtractor() };
            }

            internal abstract ICollection<Tag> Extract(HtmlNode root);

            class RandomTagExtractor : LinkedTagExtractor {

                internal override ICollection<Tag> Extract(HtmlNode root)
                {
                    return root
                        .Select("a > img")
                        .Select(s => new Tag { BestImage = s.Attr("src"), Title = s.Attr("alt") })
                        .ToList();
                }
            }

            class SubTagExtractor : LinkedTagExtractor {

                internal override ICollection<Tag> Extract(HtmlNode root)
                {
                    return root
                        .Select("td > img")
                        .Select(s => new Tag { 
                            BestImage = s.Attr("src"), 
                            Title = LinkToTagName(FindLinkToTag(s)) })
                        .ToList();
                }

                static string FindLinkToTag(HtmlNode s)
                {
                    return s.ParentNode.ParentNode.ChildNodes[1].FirstChild.Attr("href");
                }

                static string LinkToTagName(string link)
                {
                    return Uri.UnescapeDataString(Uri.UnescapeDataString(link.Substring("/tag/".Length))).Replace('+', ' ');
                }
            }
        }

        #endregion
    }
}