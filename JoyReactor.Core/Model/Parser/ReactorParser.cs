using HtmlAgilityPack;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Model.Web.Parser.Data;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Parser
{
    public class ReactorParser : SiteParser
    {
        #region Constants

        static readonly Regex RATING = new Regex("Рейтинг:\\s*<div class=\"[^\"]+\"></div>\\s*([\\d\\.]+)");
        static readonly Regex POST = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus unregistered\">", RegexOptions.Singleline);
        static readonly Regex POST_AUTHORIZED = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus", RegexOptions.Singleline);

        static readonly Regex IMAGE = new Regex("<div class=\"image\">\\s*<img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", RegexOptions.Singleline);
        static readonly Regex IMAGE_BIG = new Regex("<div class=\"image\">\\s*<a href=\"([^\"]+)\" class=\"prettyPhotoLink\" rel=\"prettyPhoto\">\\s*<img src=\"[^\"]+\" width=\"(\\d+)\" height=\"(\\d+)\"", RegexOptions.Singleline);
        static readonly Regex IMAGE_IN_POST = new Regex("<img src=\"([^\"]+/pics/post/[^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", RegexOptions.Singleline);
        static readonly Regex IMAGE_GIF = new Regex("ссылка на гифку</a><img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)");

        static readonly Regex USER_NAME = new Regex("href=\"[^\"]+user/([^\"/]+)\"", RegexOptions.Singleline);
        static readonly Regex USER_IMAGE = new Regex("src=\"([^\"]+)\" class=\"avatar\"");
        static readonly Regex TITLE = new Regex("<div class=\"post_content\"><span>([^<]*)</span>", RegexOptions.Singleline);
        static readonly Regex POST_ID = new Regex("<a href=\"/post/(\\d+)\"", RegexOptions.Singleline);
        static readonly Regex CREATED = new Regex("data\\-time=\"(\\d+)\"");

        static readonly Regex USER_ID = new Regex("userId=\"(\\d+)\"");
        static readonly Regex TEXT = new Regex("comment_txt_\\d+_\\d+\">\\s*<span>(.*?)</span>", RegexOptions.Singleline);

        static readonly Regex COMMENT_ID = new Regex("comment_txt_\\d+_(\\d+)");
        static readonly Regex TIMESTAMP = new Regex("timestamp=\"(\\d+)");

        static readonly Regex COMMENT_IMAGES = new Regex("<img src=\"(http://[^\"]+/pics/comment/)[^\"]+(\\-\\d+\\.[^\"]+)");

        static readonly Regex SUB_POSTER = new Regex("src=\"([^\"]+)\" *alt=\"[^\"]+\" *class=\"blog_avatar\" */>");
        static readonly Regex SUB_LINKED_SUBS = new Regex("<img src=\"(http://img\\d+.joyreactor\\.cc/pics/avatar/tag/\\d+)\"\\s+alt=\"([^\"]+)\"\\s*/>\\s*</td>\\s*<td>\\s*<a href=\"[^\"]+tag/([^\"]+)\"");
        static readonly Regex COUB = new Regex("<iframe src=\"http://coub.com/embed/(.+?)\" allowfullscreen=\"true\" frameborder=\"0\" width=\"(\\d+)\" height=\"(\\d+)");

        static readonly Regex COMMENT_RATING = new Regex("<span\\s*class=\"comment_rating\"\\s*comment_id=\"\\d+\">\\s*<span>—\\s*([\\d\\.]+)</span>", RegexOptions.Singleline);

        static readonly Regex CURRENT_PAGE = new Regex("<span class='current'>(\\d+)</span>");
        static readonly Regex sProfileRating = new Regex("([\\d\\.]+)");

        static readonly Regex ImageFromSharing = new Regex("\\[img\\]([^\\[]+)\\[/img\\]");

        static readonly Regex ProfileTag = new Regex("/tag/(.+)");

        static readonly string COMMENT_START = "<div class=\"post_comment_list\">";

        static readonly string[] SINGLE_TAGS = new string[] { "<br>", "<param " };

        #endregion

        public override ID.SiteParser ParserId
        {
            get { return ID.SiteParser.JoyReactor; }
        }

        public override Task<IDictionary<string, string>> LoginAsync(string username, string password)
        {
            return new LoginProvider(username, password).ComputeAsync();
        }

        public override Task<ProfileExport> ProfileAsync(string username)
        {
            return new ProfileProvider(username).ComputeAsync();
        }

        public override void ExtractPost(string postId)
        {
            new PostProvider(this, postId).Compute();
        }

        public override void ExtractTag(string tag, ID.TagType type, int? currentPageId)
        {
            new TagProvider(this, tag, type, currentPageId).Compute();
        }

        public interface IAuthStorage
        {
            Task<string> GetCurrentUserNameAsync();

            Task<IDictionary<string, string>> GetCookiesAsync();
        }

        private class TagProvider
        {
            IAuthStorage authStorage = ServiceLocator.Current.GetInstance<IAuthStorage>();
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

            ReactorParser parser;
            string tag;
            ID.TagType type;
            int? currentPageId;
            IDictionary<string, string> cookies;

            internal TagProvider(ReactorParser parser, string tag, ID.TagType type, int? currentPageId)
            {
                this.parser = parser;
                this.tag = tag;
                this.type = type;
                this.currentPageId = currentPageId;
            }

            internal void Compute()
            {
                var cookies = authStorage.GetCookiesAsync().Result;
                cookies.Add("showVideoGif2", "1");
                var html = DownloadTagPageWithCheckDomain(tag, type, currentPageId);

                ExtractTagInformation(html);

                var m = POST.Match(html);
                if (m.Success)
                {
                    do
                    {
                        ExportPost p = CreatePost(m.Groups[1].Value);
                        parser.OnNewPost(p);
                        m = m.NextMatch();
                    } while (m.Success);
                }
                else
                {
                    m = POST_AUTHORIZED.Match(html);
                    while (m.Success)
                    {
                        var p = CreatePost(m.Groups[1].Value);
                        parser.OnNewPost(p);
                        m = m.NextMatch();
                    }
                }

                if (!currentPageId.HasValue)
                    ExtractLinkedTags(html);
            }

            string DownloadTagPageWithCheckDomain(string tag, ID.TagType type, int? currentPageId)
            {
                var html = DownloadTagPage(tag, type, currentPageId);
                if (IsPageFromSecretSite(html))
                {
                    new ReactorDomainDetector().SetTagType(tag, ReactorDomainDetector.TagType.Secret);
                    return DownloadTagPage(tag, type, currentPageId);
                }
                return html;
            }
            string DownloadTagPage(string tag, ID.TagType type, int? currentPageId)
            {
                var url = GenerateUrl(type, tag, currentPageId);
                return downloader.GetText(url, new RequestParams { Cookies = cookies, UseForeignProxy = true });
            }
            Uri GenerateUrl(ID.TagType type, string tag, int? currentPage)
            {
                StringBuilder url = new StringBuilder("http://" + new ReactorDomainDetector().GetDomainForTag(tag));
                if (type == ID.TagType.Favorite)
                {
                    // TODO: перевести на асинхронную модель
                    if (tag == null)
                        tag = authStorage.GetCurrentUserNameAsync().Result;
                    url.Append("/user/").Append(Uri.EscapeDataString(tag)).Append("/favorite");
                }
                else
                {
                    if (tag != null)
                        url.Append("/tag/").Append(Uri.EscapeUriString(tag));
                    if (ID.TagType.Best == type)
                        url.Append("/best");
                    else if (ID.TagType.All == type)
                        url.Append(tag == null ? "/all" : "/new");
                }

                if ((currentPage ?? 0) > 0)
                    url.Append("/").Append(currentPage);
                return new Uri("" + url);
            }

            bool IsPageFromSecretSite(string html)
            {
                return html.Contains(">секретные разделы</a>");
            }

            void ExtractTagInformation(string html)
            {
                parser.OnNewTagInformation(new ExportTagInformation
                {
                    Image = SUB_POSTER.FirstString(html),
                    NextPage = GetNextPageOfTagList(html),
                    HasNextPage = GetNextPageOfTagList(html) > 0,
                });
            }

            int GetNextPageOfTagList(string html)
            {
                return CURRENT_PAGE.FirstInt(html) - 1;
            }

            ExportPost CreatePost(string html)
            {
                var p = new ExportPost();

                var m = IMAGE.Match(html);
                if (m.Success)
                {
                    p.Image = m.Groups[1].Value;
                    p.ImageWidth = int.Parse(m.Groups[2].Value);
                    p.ImageHeight = int.Parse(m.Groups[3].Value);
                }
                if (p.Image == null)
                {
                    m = IMAGE_GIF.Match(html);
                    if (m.Success)
                    {
                        p.Image = m.Groups[1].Value;
                        p.ImageWidth = int.Parse(m.Groups[2].Value);
                        p.ImageHeight = int.Parse(m.Groups[3].Value);
                    }
                }
                if (p.Image == null)
                {
                    m = IMAGE_BIG.Match(html);
                    if (m.Success)
                    {
                        p.Image = m.Groups[1].Value;
                        p.ImageWidth = int.Parse(m.Groups[2].Value);
                        p.ImageHeight = int.Parse(m.Groups[3].Value);
                    }
                }
                if (p.Image == null)
                {
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

                p.UserName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(html))).Replace('+', ' ');
                p.UserImage = USER_IMAGE.FirstString(html);

                p.Title = TITLE.FirstString(html);
                if (string.IsNullOrEmpty(p.Title))
                    p.Title = null;

                p.Id = POST_ID.FirstString(html);
                p.Created = CREATED.FirstLong(html) * 1000L;
                p.Rating = RATING.FirstFloat(html, CultureInfo.InvariantCulture);

                m = COUB.Match(html);
                if (m.Success)
                {
                    p.Coub = m.Groups[1].Value;
                    p.ImageWidth = int.Parse(m.Groups[2].Value);
                    p.ImageHeight = int.Parse(m.Groups[2].Value);
                }

                return p;
            }

            void ExtractLinkedTags(string html)
            {
                var m = SUB_LINKED_SUBS.Match(html);
                while (m.Success)
                {
                    var t = new ExportLinkedTag();
                    t.name = WebUtility.HtmlDecode(m.Groups[2].Value);
                    t.group = "None";
                    t.image = m.Groups[1].Value;
                    t.value = Uri.UnescapeDataString(Uri.UnescapeDataString(m.Groups[3].Value));
                    parser.OnNewLinkedTag(t);
                    m = m.NextMatch();
                }
            }
        }

        private class PostProvider
        {
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

            string postId;
            ReactorParser parser;

            public PostProvider(ReactorParser parser, string postId)
            {
                this.parser = parser;
                this.postId = postId;
            }

            internal void Compute()
            {
                var html = DownloadPostPageWithDomainCheck(postId);
                var doc = new HtmlDocument();
                doc.LoadHtml(html);

                ExportPostInformation(html);
                ExportComments(html);
            }

            string DownloadPostPageWithDomainCheck(string postId)
            {
                var domain = new ReactorDomainDetector().GetDomainForType(ReactorDomainDetector.TagType.Secret);
                var uri = new Uri(string.Format("http://{0}/post/{1}", domain, postId));
                return downloader.GetText(uri, new RequestParams { UseForeignProxy = true });
            }

            void ExportPostInformation(string html)
            {
                var p = new ExportPostInformation();

                p.User = new ExportUser
                {
                    Name = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(html))).Replace('+', ' '),
                    Avatar = USER_IMAGE.FirstString(html),
                };

                p.Title = TITLE.FirstString(html);
                if (string.IsNullOrWhiteSpace(p.Title))
                    p.Title = null;
                p.Created = (CREATED.FirstLong(html) * 1000L).DateTimeFromUnixTimestampMs();
                p.Rating = RATING.FirstFloat(html, CultureInfo.InvariantCulture);

                var attachments = new List<ExportAttachment>();
                ExportPostAttachments(html, attachments);
                ExportCoub(html, attachments);
                p.Attachments = attachments.ToArray();

                parser.OnNewPostInformation(p);
            }

            void ExportPostAttachments(string html, List<ExportAttachment> attachments)
            {
                string image = null;
                int width = 0, height = 0;

                var m = IMAGE_IN_POST.Match(html);
                if (m.Success)
                {
                    image = m.Groups[1].Value;
                    width = int.Parse(m.Groups[2].Value);
                    height = int.Parse(m.Groups[3].Value);
                }
                if (image == null)
                {
                    m = IMAGE_GIF.Match(html);
                    if (m.Success)
                    {
                        image = m.Groups[1].Value;
                        width = int.Parse(m.Groups[2].Value);
                        height = int.Parse(m.Groups[3].Value);
                    }
                }
                if (image == null)
                {
                    m = new Regex("\\[img\\]([^\\[]+)\\[/img\\]").Match(html);
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
                    //                image = Regex.Replace(image, "(http[s]?)://.+?/", "$1://joyreactor.com/");
                    image = Regex.Replace(image, @"(http[s]?://.*?)[^\.]+\.[^\./]+/", "$1joyreactor.com/");

                    attachments.Add(
                        new ExportAttachment
                        {
                            Image = image,
                            Width = width,
                            Height = height,
                        });
                }
            }

            void ExportCoub(string html, List<ExportAttachment> attachments)
            {
                int i = html.IndexOf("class=\"post_comment_list\"");
                if (i < 0)
                    throw new Exception("Can't find comments begin");
                var m = COUB.Match(html.Substring(0, i));
                if (m.Success)
                {
                    attachments.Add(new ExportAttachment
                    {
                        Url = m.Groups[1].Value,
                        Width = int.Parse(m.Groups[2].Value),
                        Height = int.Parse(m.Groups[3].Value),
                    });
                }
            }

            void ExportComments(string html)
            {
                int pos = html.IndexOf(COMMENT_START) + COMMENT_START.Length;
                pos = SkipHtmlTag(html, pos);
                ReadChildComments(html, pos, null, parser.OnNewComment);
            }

            int ReadChildComments(string html, int position, string parentId, Action<ExportPostComment> callback)
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

                    var c = GetComment(html, position, end);
                    if (parentId != null)
                        c.ParentIds = new string[] { parentId };
                    callback(c);

                    end = SkipHtmlTag(html, end + 1);
                    end = ReadChildComments(html, end, c.Id, callback);
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

            ExportPostComment GetComment(string html, int start, int end)
            {
                var s = html.Substring(start, end + 1 - start);
                var c = new ExportPostComment();

                c.Id = COMMENT_ID.FirstString(s);
                c.Content = TEXT.FirstString(s)?.Replace("<br>", Environment.NewLine);
                c.Created = (TIMESTAMP.FirstLong(s) * 1000L).DateTimeFromUnixTimestampMs();
                c.Rating = COMMENT_RATING.FirstFloat(s, CultureInfo.InvariantCulture);

                c.User = new ExportUser
                {
                    Name = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(s))).Replace('+', ' '),
                    Avatar = "http://img0.joyreactor.cc/pics/avatar/user/" + USER_ID.FirstString(s),
                };

                var m = COMMENT_IMAGES.Match(s);
                if (m.Success)
                {
                    var u = m.Groups[1].Value + m.Groups[2].Value;
                    c.Attachments = new[] { new ExportAttachment { Image = u } };
                }
                else
                {
                    c.Attachments = new ExportAttachment[0];
                }

                return c;
            }

        }

        private class LoginProvider
        {
            IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();

            string username;
            string password;

            internal LoginProvider(string username, string password)
            {
                this.username = username;
                this.password = password;
            }

            public async Task<IDictionary<string, string>> ComputeAsync()
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

                return hs;
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

            string username;

            internal ProfileProvider(string username)
            {
                this.username = username;
            }

            public async Task<ProfileExport> ComputeAsync()
            {
                var url = new Uri("http://joyreactor.cc/user/" + Uri.EscapeDataString(username));
                var doc = await downloader.GetDocumentAsync(url);

                var p = new ProfileExport();
                p.UserName = username;

                var sidebar = doc.GetElementbyId("sidebar");
                var div = sidebar.Descendants("div")
                    .Where(s => s.GetClass() == "user")
                    .SelectMany(s => s.ChildNodes)
                    .First(s => s.Name == "img");
                p.UserImage = new Uri(div.Attributes["src"].Value);

                p.Stars = sidebar.Select("div.star-0").Count();
                p.NextStarProgress = sidebar
                    .Select("div.poll_res_bg_active")
                    .Select(s => s.Attr("style"))
                    .Select(s => Regex.Match(s, ":(\\d+)").Groups[1].Value)
                    .Select(s => float.Parse(s) / 100f)
                    .First();

                p.Awards = sidebar
                    .Select("div.user-awards > img")
                    .Select(s => new ProfileExport.Award { Image = s.Attr("src"), Name = s.Attr("alt") })
                    .ToList();

                div = doc.GetElementbyId("rating-text").ChildNodes.First(s => s.Name == "b");
                var n = sProfileRating.Match(div.InnerText.Replace(" ", "")).Groups[1].Value;
                p.Rating = float.Parse(n, CultureInfo.InvariantCulture);

                div = sidebar.ChildNodes.Where(s => s.HasChildNodes).FirstOrDefault(s => "Читает" == s.ChildNodes[0].InnerText);
                if (div != null)
                {
                    p.ReadingTags = div.Descendants("a").Select(s => new ProfileExport.TagExport
                    {
                        Title = s.InnerText,
                        Tag = Uri.UnescapeDataString(Uri.UnescapeDataString(ProfileTag.FirstString(s.GetHref()))).Replace('+', ' '),
                    }).ToList();
                }

                return p;
            }
        }
    }
}