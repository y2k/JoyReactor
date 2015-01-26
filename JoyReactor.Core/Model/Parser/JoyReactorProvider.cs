using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Linq;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Parser
{
    class JoyReactorProvider
    {
        public Task LoadTagAsync(ID id, int? currentPage)
        {
            return new TagProvider(id, currentPage).Compute();
        }

        public Task LoagPostAsync(string postId)
        {
            return new PostProvider(postId).Compute();
        }

        class TagProvider
        {
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
                pageHtml = await DownloadTagPageWithCheckDomainAsync();
                await ExtractTagInformationAsync();
                await ExtractPostsAsync();
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
                return storage.UpdateTagInformationAsync(image, nextPage, hasNextPage);
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
            }

            #endregion
        }

        class PostProvider
        {
            IAuthStorage authStorage = ServiceLocator.Current.GetInstance<IAuthStorage>();
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
            IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();

            string postId;
            string htmlPage;

            internal PostProvider(string postId)
            {
                this.postId = postId;
            }

            internal async Task Compute()
            {
                await DownloadHtmlPageAsync();

                await SavePostInformation();
                await SavePostAttachments();
                await ExportComments();
            }

            async Task DownloadHtmlPageAsync()
            {
                var uri = new Uri("http://joyreactor.cc/post/" + postId);
                htmlPage = await downloader.GetTextAsync(uri, new RequestParams { UseForeignProxy = true });
            }

            #region Save post information

            async Task SavePostInformation()
            {
                var p = new Post();

                var USER_NAME = new Regex("href=\"[^\"]+user/([^\"/]+)\"", RegexOptions.Singleline);
                p.UserName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(htmlPage))).Replace('+', ' ');

                var USER_IMAGE = new Regex("src=\"([^\"]+)\" class=\"avatar\"");
                p.UserImage = USER_IMAGE.FirstString(htmlPage);

                var TITLE = new Regex("<div class=\"post_content\"><span>([^<]*)</span>", RegexOptions.Singleline);
                p.Title = TITLE.FirstString(htmlPage);
                if (string.IsNullOrWhiteSpace(p.Title)) p.Title = null;

                var CREATED = new Regex("data\\-time=\"(\\d+)\"");
                p.Created = CREATED.FirstLong(htmlPage) * 1000L;

                var RATING = new Regex("Рейтинг:\\s*<div class=\"[^\"]+\"></div>\\s*([\\d\\.]+)");
                p.Rating = RATING.FirstFloat(htmlPage, CultureInfo.InvariantCulture);

                await storage.SaveNewOrUpdatePostAsync(p);
            }

            async Task SavePostAttachments()
            {
                var attachments = ExportPostImages().Union(ExportPostCoubs());
                await storage.ReplacePostAttachments(null, attachments);
            }

            IEnumerable<Attachment> ExportPostImages()
            {
                string image = null;
                int width = 0, height = 0;

                var IMAGE_IN_POST = new Regex("<img src=\"([^\"]+/pics/post/[^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", RegexOptions.Singleline);
                var m = IMAGE_IN_POST.Match(htmlPage);
                if (m.Success)
                {
                    image = m.Groups[1].Value;
                    width = int.Parse(m.Groups[2].Value);
                    height = int.Parse(m.Groups[3].Value);
                }
                if (image == null)
                {
                    var IMAGE_GIF = new Regex("ссылка на гифку</a><img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)");
                    m = IMAGE_GIF.Match(htmlPage);
                    if (m.Success)
                    {
                        image = m.Groups[1].Value;
                        width = int.Parse(m.Groups[2].Value);
                        height = int.Parse(m.Groups[3].Value);
                    }
                }
                if (image == null)
                {
                    m = new Regex("\\[img\\]([^\\[]+)\\[/img\\]").Match(htmlPage);
                    if (m.Success)
                    {
                        image = m.Groups[1].Value;
                        width = 512;
                        height = 512;
                    }
                }
                if (image != null)
                {
                    image = Regex.Replace(image, @"/pics/post/.+-(\d+\.[\d\w]+)", "/pics/post/-$1");
                    image = Regex.Replace(image, @"(http[s]?://.*?)[^\.]+\.[^\./]+/", "$1joyreactor.com/");

                    yield return new Attachment
                    {
                        PreviewImageUrl = image,
                        Url = image,
                        PreviewImageWidth = width,
                        PreviewImageHeight = height,
                    };
                }
            }

            IEnumerable<Attachment> ExportPostCoubs()
            {
                int i = htmlPage.IndexOf("class=\"post_comment_list\"");
                if (i < 0)
                    throw new Exception("Can't find comments begin");
                var COUB = new Regex("<iframe src=\"http://coub.com/embed/(.+?)\" allowfullscreen=\"true\" frameborder=\"0\" width=\"(\\d+)\" height=\"(\\d+)");
                var m = COUB.Match(htmlPage.Substring(0, i));
                if (m.Success)
                {
                    yield return new Attachment
                    {
                        PreviewImageUrl = m.Groups[1].Value,
                        Url = m.Groups[1].Value,
                        PreviewImageWidth = int.Parse(m.Groups[2].Value),
                        PreviewImageHeight = int.Parse(m.Groups[3].Value),
                    };
                }
            }

            #endregion

            #region Save post comments

            async Task ExportComments()
            {
                await storage.RemovePostComments(postId);

                const string COMMENT_START = "<div class=\"post_comment_list\">";
                int pos = htmlPage.IndexOf(COMMENT_START) + COMMENT_START.Length;

                pos = SkipHtmlTag(htmlPage, pos);
                await ReadChildComments(htmlPage, pos, null);
            }

            async Task<int> ReadChildComments(string html, int position, string parentId)
            {
                int end;
                int initPosition = position;
                while (true)
                {
                    end = ReadTag(html, position);
                    if (end < 0)
                        break;

                    // Оптимизированная (по памяти и CPU) проверка случая когда "нет комментариев"
                    if (parentId == null && position == initPosition)
                    {
                        if (end - position < 100 && html.Substring(position, end - position).Contains("нет комментариев"))
                            return position;
                    }

                    string commentId = await GetComment(html, position, end, parentId);

                    end = SkipHtmlTag(html, end + 1);
                    end = await ReadChildComments(html, end, commentId);
                    position = SkipHtmlTag(html, end);
                }
                return position;
            }

            int SkipHtmlTag(string html, int position)
            {
                return html.IndexOf('>', position) + 1;
            }

            int ReadTag(string html, int position)
            {
                int level = 0;
                do
                {
                    int i = html.IndexOf('<', position);
                    int endTag = html.IndexOf('>', i + 1);

                    var SINGLE_TAGS = new string[] { "<br>", "<param " };
                    if (SINGLE_TAGS.Any(s => s == html.Substring(i, s.Length)))
                    {
                        position = html.IndexOf('>', i);
                        if (position < 0)
                            throw new Exception();
                        position++;
                        continue;
                    }
                    else if (html[endTag - 1] == '/')
                    {
                        position = endTag + 1;
                        continue;
                    }

                    level += html[i + 1] == '/' ? -1 : 1;
                    position = i + 1;
                } while (level > 0);
                return level < 0 ? -1 : html.IndexOf('>', position);
            }

            async Task<string> GetComment(string html, int start, int end, string parentId)
            {
                var s = html.Substring(start, end + 1 - start);
                var c = new Comment();

                var COMMENT_ID = new Regex("comment_txt_\\d+_(\\d+)");
                c.CommentId = COMMENT_ID.FirstString(s);

                var TEXT = new Regex("comment_txt_\\d+_\\d+\">\\s*<span>(.*?)</span>", RegexOptions.Singleline);
                c.Text = TEXT.FirstString(s)?.Replace("<br>", Environment.NewLine);

                var TIMESTAMP = new Regex("timestamp=\"(\\d+)");
                c.Created = TIMESTAMP.FirstLong(s) * 1000L;

                var COMMENT_RATING = new Regex("<span\\s*class=\"comment_rating\"\\s*comment_id=\"\\d+\">\\s*<span>—\\s*([\\d\\.]+)</span>", RegexOptions.Singleline);
                c.Rating = COMMENT_RATING.FirstFloat(s, CultureInfo.InvariantCulture);

                var USER_NAME = new Regex("href=\"[^\"]+user/([^\"/]+)\"", RegexOptions.Singleline);
                c.UserName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(s))).Replace('+', ' ');
                var USER_ID = new Regex("userId=\"(\\d+)\"");
                c.UserImage = "http://img0.joyreactor.cc/pics/avatar/user/" + USER_ID.FirstString(s);

                var COMMENT_IMAGES = new Regex("<img src=\"(http://[^\"]+/pics/comment/)[^\"]+(\\-\\d+\\.[^\"]+)");
                var m = COMMENT_IMAGES.Match(s);
                var attchs = m.Success ? new string[] { m.Groups[1].Value + m.Groups[2].Value } : new string[0];

                await storage.SaveNewPostCommentAsync(postId, parentId, c, attchs);

                return c.CommentId;
            }

            #endregion
        }

        internal interface IStorage
        {
            Task SaveNewOrUpdatePostAsync(Post post);

            Task UpdateTagInformationAsync(string image, int nextPage, bool hasNextPage);

            Task ReplacePostAttachments(string postId, IEnumerable<Attachment> attachments);

            Task RemovePostComments(string postId);

            Task SaveNewPostCommentAsync(string postId, string parrentCommentId, Comment comment, string[] attachments);
        }

        internal interface IAuthStorage
        {
            Task<string> GetCurrentUserNameAsync();

            Task<IDictionary<string, string>> GetCookiesAsync();
        }
    }
}