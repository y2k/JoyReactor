using HtmlAgilityPack;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Parser
{
    class JoyReactorProvider
    {
        #region New instance factory

        JoyReactorProvider() { }

        internal static JoyReactorProvider Create()
        {
            return new JoyReactorProvider();
        }

        #endregion

        public Task LoadTagAndPostListAsync(ID id, IListStorage listStorage)
        {
            return new TagProvider(id, listStorage).ComputeAsync();
        }

        public Task LoadPostAsync(string postId)
        {
            return new PostProvider(postId).ComputeAsync();
        }

        public Task LoginAsync(string username, string password)
        {
            return new LoginProvider(username, password).ComputeAsync();
        }

        public Task LoadCurrentUserProfileAsync()
        {
            return new ProfileProvider().ComputeAsync();
        }

        class TagProvider
        {
            IAuthStorage authStorage = ServiceLocator.Current.GetInstance<IAuthStorage>();
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
            IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();

            IListStorage listStorage;
            ID id;
            string pageHtml;

            public TagProvider(ID id, IListStorage listStorage)
            {
                this.id = id;
                this.listStorage = listStorage;
            }

            internal async Task ComputeAsync()
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

                int currentPage = await storage.GetNextPageForTagAsync(id);
                if (currentPage > 0) url.Append("/").Append(currentPage);
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

            internal async Task ComputeAsync()
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
                var attachments = await Task.Run(() => ExportPostImages().Union(ExportPostCoubs()).ToList());
                await storage.ReplacePostAttachments(postId, attachments);
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
                await ReadChildComments(htmlPage, pos, 0);
            }

            async Task<int> ReadChildComments(string html, int position, int parentId)
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

                    var commentId = await SaveComment(html, position, end, parentId);

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

            async Task<int> SaveComment(string html, int start, int end, int parentId)
            {
                var s = html.Substring(start, end + 1 - start);
                var c = new Comment();

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

                return c.Id;
            }

            #endregion
        }

        private class LoginProvider
        {
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
            IAuthStorage authStorage = ServiceLocator.Current.GetInstance<IAuthStorage>();

            string username;
            string password;

            internal LoginProvider(string username, string password)
            {
                this.username = username;
                this.password = password;
            }

            public async Task ComputeAsync()
            {
                var loginPage = await downloader.ExecuteAsync(new Uri("http://joyreactor.cc/login"));
                var csrf = ExtractCsrf(loginPage.Data);

                var hs = await downloader.PostForCookiesAsync(
                            new Uri("http://joyreactor.cc/login"),
                            new RequestParams
                            {
                                Cookies = loginPage.Cookies,
                                Referer = new Uri("http://joyreactor.cc/login"),
                                Form = new Dictionary<string, string>
                                {
                                    ["signin[username]"] = username,
                                    ["signin[password]"] = password,
                                    ["signin[remember]"] = "on",
                                    ["signin[_csrf_token]"] = csrf,
                                }
                            });

                if (!hs.ContainsKey("joyreactor"))
                    throw new Exception();

                await authStorage.SaveCookieToDatabaseAsync(username, hs);
            }

            string ExtractCsrf(Stream data)
            {
                using (data)
                {
                    var doc = new HtmlDocument();
                    doc.Load(data);
                    return doc.GetElementbyId("signin__csrf_token").Attributes["value"].Value;
                }
            }
        }

        private class ProfileProvider
        {
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
            IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
            IAuthStorage authStorage = ServiceLocator.Current.GetInstance<IAuthStorage>();

            public async Task ComputeAsync()
            {
                var username = await authStorage.GetCurrentUserNameAsync();
                if (username == null)
                    throw new NotLogedException();
                var url = new Uri("http://joyreactor.cc/user/" + Uri.EscapeDataString(username));
                var doc = await downloader.GetDocumentAsync(url);

                var p = new Profile();
                p.UserName = username;

                var sidebar = doc.GetElementbyId("sidebar");
                var div = sidebar.Descendants("div")
                    .Where(s => s.GetClass() == "user")
                    .SelectMany(s => s.ChildNodes)
                    .First(s => s.Name == "img");
                p.UserImage = div.Attributes["src"].Value;

                p.Stars = sidebar.Select("div.star-0").Count();
                p.NextStarProgress = sidebar
                    .Select("div.poll_res_bg_active")
                    .Select(s => s.Attr("style"))
                    .Select(s => Regex.Match(s, ":(\\d+)").Groups[1].Value)
                    .Select(s => float.Parse(s) / 100f)
                    .First();

                div = doc.GetElementbyId("rating-text").ChildNodes.First(s => s.Name == "b");

                var profileRatingRx = new Regex("([\\d\\.]+)");
                var n = profileRatingRx.Match(div.InnerText.Replace(" ", "")).Groups[1].Value;
                p.Rating = float.Parse(n, CultureInfo.InvariantCulture);

                // TODO: Добавить обработку новых полей профиля сразу после рефакторинга
                //p.Awards = sidebar
                //    .Select("div.user-awards > img")
                //    .Select(s => new ProfileExport.Award { Image = s.Attr("src"), Name = s.Attr("alt") })
                //    .ToList();

                div = sidebar.ChildNodes.Where(s => s.HasChildNodes).FirstOrDefault(s => "Читает" == s.ChildNodes[0].InnerText);
                if (div != null)
                {
                    var profileTagRx = new Regex("/tag/(.+)");
                    var readingTags = div.Descendants("a").Select(s => UnescapeTagName(profileTagRx.FirstString(s.GetHref())));
                    await storage.ReplaceCurrentUserReadingTagsAsync(readingTags);
                }

                await storage.SaveNewOrUpdateProfileAsync(p);
            }

            private static string UnescapeTagName(string tag)
            {
                return tag
                    .UnescapeDataString()
                    .UnescapeDataString()
                    .Replace('+', ' ');
            }
        }

        internal interface IListStorage
        {
            void AddPost(Post post);

            Task CommitAsync();
        }

        internal interface IStorage
        {
            Task SaveNewOrUpdatePostAsync(Post post);

            Task UpdateTagInformationAsync(ID id, string image, int nextPage, bool hasNextPage);

            Task ReplacePostAttachments(string postId, List<Attachment> attachments);

            Task RemovePostComments(string postId);

            Task SaveNewPostCommentAsync(string postId, int parrentCommentId, Comment comment, string[] attachments);

            Task SaveNewOrUpdateProfileAsync(Profile profile);

            Task ReplaceCurrentUserReadingTagsAsync(IEnumerable<string> readingTags);

            Task<int> GetNextPageForTagAsync(ID id);
        }

        internal interface IAuthStorage
        {
            Task<string> GetCurrentUserNameAsync();

            Task<IDictionary<string, string>> GetCookiesAsync();

            Task SaveCookieToDatabaseAsync(string username, IDictionary<string, string> cookies);
        }
    }
}