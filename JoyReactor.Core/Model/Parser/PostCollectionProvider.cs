using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Parser
{
    public class PostCollectionProvider
    {
        public const int FirstPage = 0;

        public int NextPage { get; set; }

        public List<Post> Posts { get; set; }

        ID id;
        int page;
        string pageHtml;

        public PostCollectionProvider(ID id, int page)
        {
            this.page = page;
            this.id = id;
        }

        public async Task DownloadFromWebAsync()
        {
            pageHtml = await new PageDownloader(id, page).DownloadAsync();
            NextPage = GetNextPageOfTagList();
            Posts = new PageCollectionParser(pageHtml).Parse();
        }

        int GetNextPageOfTagList()
        {
            var currentPageRx = new Regex("<span class='current'>(\\d+)</span>");
            return currentPageRx.FirstInt(pageHtml) - 1;
        }

        class PageDownloader
        {
            WebDownloader downloader = ServiceLocator.Current.GetInstance<WebDownloader>();
            IProviderAuthStorage authStorage = ServiceLocator.Current.GetInstance<IProviderAuthStorage>();
            ID id;
            int page;

            internal PageDownloader(ID id, int page)
            {
                this.page = page;
                this.id = id;
            }

            public async Task<string> DownloadAsync()
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

                int currentPage = page;
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
        }

        class PageCollectionParser
        {
            string pageHtml;

            public PageCollectionParser(string pageHtml)
            {
                this.pageHtml = pageHtml;
                throw new NotImplementedException();
            }

            public List<Post> Parse()
            {
                return GetPostHtmlList().Select(s => ParserHtmlToPost(s)).ToList();
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

            Post ParserHtmlToPost(string html)
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

                return p;
            }
        }
    }
}