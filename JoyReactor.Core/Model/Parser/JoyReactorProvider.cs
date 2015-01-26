using System;
using Microsoft.Practices.ServiceLocation;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Web;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.DTO;
using System.Globalization;
using System.Text;

namespace JoyReactor.Core.Model.Parser
{
    class JoyReactorProvider
    {
        public Task LoadTagAsync(ID id, int? currentPage)
        {
            return new TagProvider(id, currentPage).Compute();
        }

        class TagProvider
        {

            static readonly Regex SUB_POSTER = new Regex("src=\"([^\"]+)\" *alt=\"[^\"]+\" *class=\"blog_avatar\" */>");
            static readonly Regex CURRENT_PAGE = new Regex("<span class='current'>(\\d+)</span>");

            static readonly Regex POST = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus unregistered\">", RegexOptions.Singleline);
            static readonly Regex POST_AUTHORIZED = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus", RegexOptions.Singleline);

            IAuthStorage authStorage = ServiceLocator.Current.GetInstance<IAuthStorage>();
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
            IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();

            ID id;
            int? currentPage;
            string pageHtml;

            public TagProvider(ID id, int? currentPage)
            {
                this.currentPage = currentPage;
                this.id = id;
            }

            internal async Task Compute()
            {
                pageHtml = await DownloadHtmlPageAsync();
                await ExtractTagInformationAsync();
                await ExtractPostsAsync();
            }

            Task<string> DownloadHtmlPageAsync()
            {
                throw new NotImplementedException();
            }

            Task ExtractTagInformationAsync()
            {
                var image = SUB_POSTER.FirstString(pageHtml);
                var nextPage = GetNextPageOfTagList();
                var hasNextPage = GetNextPageOfTagList() > 0;
                return storage.UpdateTagInformationAsync(image, nextPage, hasNextPage);
            }

            int GetNextPageOfTagList()
            {
                return CURRENT_PAGE.FirstInt(pageHtml) - 1;
            }

            Task ExtractPostsAsync()
            {
                foreach (var htmlPost in GetPostHtmlList())
                {
                    //
                }

                throw new NotImplementedException();
            }

            IEnumerable<string> GetPostHtmlList()
            {
                var m = POST.Match(pageHtml);
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
                    m = POST_AUTHORIZED.Match(pageHtml);
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

                await storage.SaveOrUpdatePostAsync(p);
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

                if ((currentPage ?? 0) > 0)
                    url.Append("/").Append(currentPage);
                return new Uri("" + url);
            }

            bool IsPageFromSecretSite(string html)
            {
                return html.Contains(">секретные разделы</a>");
            }

            #endregion

            async Task<IDictionary<string, string>> GetCookiesAsync()
            {
                var cookies = await authStorage.GetCookiesAsync();
                cookies.Add("showVideoGif2", "1");
                return cookies;
            }
        }

        internal interface IStorage
        {

            Task SaveOrUpdatePostAsync(Post post);

            Task UpdateTagInformationAsync(string image, int nextPage, bool hasNextPage);
        }

        internal interface IAuthStorage
        {
            Task<string> GetCurrentUserNameAsync();

            Task<IDictionary<string, string>> GetCookiesAsync();
        }
    }
}