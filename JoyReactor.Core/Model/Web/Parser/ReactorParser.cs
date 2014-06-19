using HtmlAgilityPack;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Web;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;

namespace JoyReactor.Core.Model.Parser
{
    public class ReactorParser : ISiteParser
    {
        #region Constants

        private static readonly Regex RATING = new Regex("Рейтинг:\\s*<div class=\"[^\"]+\"></div>\\s*([\\d\\.]+)");
        private static readonly Regex POST = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus unregistered\">", RegexOptions.Singleline);
        private static readonly Regex POST_AUTHORIZED = new Regex("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus", RegexOptions.Singleline);

        private static readonly Regex IMAGE = new Regex("<div class=\"image\">\\s*<img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", RegexOptions.Singleline);
        private static readonly Regex IMAGE_BIG = new Regex("<div class=\"image\">\\s*<a href=\"([^\"]+)\" class=\"prettyPhotoLink\" rel=\"prettyPhoto\">\\s*<img src=\"[^\"]+\" width=\"(\\d+)\" height=\"(\\d+)\"", RegexOptions.Singleline);
        private static readonly Regex IMAGE_IN_POST = new Regex("<img src=\"([^\"]+/pics/post/[^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", RegexOptions.Singleline);
        private static readonly Regex IMAGE_GIF = new Regex("ссылка на гифку</a><img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)");

        private static readonly Regex USER_NAME = new Regex("href=\"[^\"]+user/([^\"/]+)\"", RegexOptions.Singleline);
        private static readonly Regex USER_IMAGE = new Regex("src=\"([^\"]+)\" class=\"avatar\"");
        private static readonly Regex TITLE = new Regex("<div class=\"post_content\"><span>([^<]*)</span>", RegexOptions.Singleline);
        private static readonly Regex POST_ID = new Regex("<a href=\"/post/(\\d+)\"", RegexOptions.Singleline);
        private static readonly Regex CREATED = new Regex("data\\-time=\"(\\d+)\"");

        private static readonly Regex USER_ID = new Regex("userId=\"(\\d+)\"");
        private static readonly Regex TEXT = new Regex("comment_txt_\\d+_\\d+\">\\s*<span>(.*?)</span>", RegexOptions.Singleline);

        private static readonly Regex COMMENT_ID = new Regex("comment_txt_\\d+_(\\d+)");
        private static readonly Regex TIMESTAMP = new Regex("timestamp=\"(\\d+)");

        private static readonly Regex COMMENT_IMAGES = new Regex("<img src=\"(http://[^\"]+/pics/comment/)[^\"]+(\\-\\d+\\.[^\"]+)");

        private static readonly Regex TAGS = new Regex("<a title=\"(.+?)\" data\\-ids=");
        private static readonly Regex TAGS_INTEREST = new Regex(">([^\\(<>]+)\\(\\d+\\)</");

        private static readonly Regex SUB_POSTER = new Regex("src=\"([^\"]+)\" *alt=\"[^\"]+\" *class=\"blog_avatar\" */>");

        private static readonly Regex SIMILAR_POST = new Regex("<td class=\"similar_post\">(.+?)</td>", RegexOptions.Singleline);
        private static readonly Regex SIMILAR_POST_ID = new Regex("<a href=\"/post/(\\d+)\">");
        private static readonly Regex SIMILAR_POST_IMAGE = new Regex("<img src=\"([^\"]+)");

        private static readonly Regex SUB_LINKED_SUBS = new Regex("<img src=\"(http://img\\d+.joyreactor\\.cc/pics/avatar/tag/\\d+)\"\\s+alt=\"([^\"]+)\"\\s*/>\\s*</td>\\s*<td>\\s*<a href=\"[^\"]+tag/([^\"]+)\"");

        private static readonly Regex COUB = new Regex("<iframe src=\"http://coub.com/embed/(.+?)\" allowfullscreen=\"true\" frameborder=\"0\" width=\"(\\d+)\" height=\"(\\d+)");

        private static readonly Regex SIMILAR_POST_TITLE = new Regex("<img src=\"[^\"]+\" alt=\"([^\"]+)");
        private static readonly Regex SIMILAR_POST_TITLE2 = new Regex("<a href=\"[^\"]*/tag/[^\"]+\">\\s*([^<]+)\\s*</a>");
        private static readonly Regex SIMILAR_POST_TITLE3 = new Regex("<a href=\"http://([\\w\\d]+)\\.joyreactor\\.cc/\">");

        private static readonly Regex COMMENT_RATING = new Regex("<span\\s*class=\"comment_rating\"\\s*comment_id=\"\\d+\">\\s*<span>—\\s*([\\d\\.]+)</span>", RegexOptions.Singleline);

        private static readonly Regex CURRENT_PAGE = new Regex("<span class='current'>(\\d+)</span>");
        private static readonly Regex sProfileRating = new Regex("([\\d\\.]+)");

        private static readonly Regex ImageFromSharing = new Regex("\\[img\\]([^\\[]+)\\[/img\\]");

        private static readonly Regex ProfileTag = new Regex("/tag/(.+)");

        private static readonly string COMMENT_START = "<div class=\"post_comment_list\">";

        private static readonly string[] SINGLE_TAGS = new string[] { "<br>", "<param " };

        #endregion

		private IWebDownloader downloader = InjectService.Locator.GetInstance<IWebDownloader>();

        #region Public methods

        public ID.SiteParser ParserId
        {
            get { return ID.SiteParser.JoyReactor; }
        }

        //public IDictionary<string, string> Login(string username, string password)
        //{
        //    var doc = downloader.Get(new Uri("http://joyreactor.cc/login"));
        //    var csrf = doc.GetElementById("signin__csrf_token").Attributes["value"].Value;

        //    var hs = downloader.PostForCookies(
        //        new Uri("http://joyreactor.cc/login"),
        //        new RequestParams
        //        {
        //            Referer = new Uri("http://joyreactor.cc/login"),
        //            Form = new Dictionary<string, string>
        //            {
        //                { "signin[username]", username },
        //                { "signin[password]", password },
        //                { "signin[remember]", "on" },
        //                { "signin[_csrf_token]", csrf },
        //            }
        //        });

        //    if (!hs.ContainsKey("joyreactor"))
        //        throw new Exception();

        //    return hs;
        //}

        public IDictionary<string, string> Login(string username, string password)
        {
            var doc = downloader.GetDocument(new Uri("http://joyreactor.cc/login"));
            var csrf = doc.Document.GetElementById("signin__csrf_token").Attributes["value"].Value;

            var hs = downloader.PostForCookies(
                new Uri("http://joyreactor.cc/login"),
                new RequestParams
                {
                    Cookies = doc.Cookies,
                    Referer = new Uri("http://joyreactor.cc/login"),
                    Form = new Dictionary<string, string>
					{
						{ "signin[username]", username },
						{ "signin[password]", password },
						{ "signin[remember]", "on" },
						{ "signin[_csrf_token]", csrf },
					}
                });

            if (!hs.ContainsKey("joyreactor"))
                throw new Exception();

            return hs;
        }

        public void ExtractTagPostCollection(ID.TagType type, string tag, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
            ExtractPostCoollection(type, tag, lastLoadedPage, cookies, callback);
        }

        public ProfileExport Profile(string username)
        {
            var url = new Uri("http://joyreactor.cc/user/" + Uri.EscapeDataString(username));
            var doc = downloader.Get(url);

            var p = new ProfileExport();
            p.Username = username;

            var sidebar = doc.GetElementById("sidebar");
            var div = sidebar.Descendants("div")
                .Where(s => s.GetClass() == "user")
                .SelectMany(s => s.ChildNodes)
                .First(s => s.Name == "img");
            p.Image = new Uri(div.Attributes["src"].Value);

            div = doc.GetElementById("rating-text").ChildNodes.First(s => s.Name == "b");
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

        public void ExtractPost(string id, Action<PostExportState> callback)
        {
            //var cookies = cookieHolder.Get(GetType());
            //cookies.Add("showVideoGif2", "1");

            var html = downloader.GetText(new Uri(string.Format("http://joyreactor.cc/post/{0}", id)));
            var doc = new HtmlDocument();
            doc.LoadHtml(html);

            callback(new PostExportState { State = PostExportState.ExportState.Begin });

            {
                var p = new PostExportState();

                var m = IMAGE_IN_POST.Match(html);
                if (m.Success)
                {
                    p.image = m.Groups[1].Value;
                    p.imageWidth = int.Parse(m.Groups[2].Value);
                    p.imageHeight = int.Parse(m.Groups[3].Value);
                }
                if (p.image == null)
                {
                    m = IMAGE_GIF.Match(html);
                    if (m.Success)
                    {
                        p.image = m.Groups[1].Value;
                        p.imageWidth = int.Parse(m.Groups[2].Value);
                        p.imageHeight = int.Parse(m.Groups[3].Value);
                    }
                }
                if (p.image == null)
                {
                    m = new Regex("\\[img\\]([^\\[]+)\\[/img\\]").Match(html);
                    if (m.Success)
                    {
                        p.image = m.Groups[1].Value;
                        p.imageWidth = 512;
                        p.imageHeight = 512;
                    }
                }

                if (p.image != null) p.image = Regex.Replace(p.image, "/pics/post/.+-(\\d+\\.[\\d\\w]+)", "/pics/post/-$1");

                p.userName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(html))).Replace('+', ' ');
                p.userImage = USER_IMAGE.FirstString(html);

                p.title = TITLE.FirstString(html);
                if (string.IsNullOrWhiteSpace(p.title)) p.title = null;

                p.created = CREATED.FirstLong(html) * 1000L;
                p.rating = RATING.FirstFloat(html, CultureInfo.InvariantCulture);

                int i = html.IndexOf("class=\"post_comment_list\"");
                if (i < 0) throw new Exception("Can't find comments begin");

                m = COUB.Match(html.Substring(0, i));
                if (m.Success)
                {
                    p.coub = m.Groups[1].Value;
                    p.imageWidth = int.Parse(m.Groups[2].Value);
                    p.imageHeight = int.Parse(m.Groups[3].Value);
                }

                p.State = PostExportState.ExportState.Info;
                callback(p);
            }

            {
                int pos = html.IndexOf(COMMENT_START) + COMMENT_START.Length;
                pos = skipHtmlTag(html, pos);
                readChildComments(html, pos, null, comment =>
                {
                    callback(new PostExportState { State = PostExportState.ExportState.Comment, Comment = comment });
                });
            }

            //			{ // TODO
            //				for (Element g : doc.select("div.sidebar_block")) {
            //					String gt = XpathUtils.innerTextTrim(g, "h2.sideheader.random");
            //					if (gt != null) {
            //						for (Element e : g.select("tr")) {
            //							LinkedTag t = new LinkedTag();
            //							t.name = XpathUtils.innerTextTrim(e, "a");
            //							t.group = gt;
            //							t.image = XpathUtils.firstUrl(e, "img", "src");
            //							t.value = PatternUtils.group(Pattern.compile("/tag/(.+)"), XpathUtils.firstAttr(e, "a", "href"));
            //							callbacks.onExtractTags(t);
            //						}
            //					}
            //				}
            //			}

            //			{
            //				//
            //				var m = SIMILAR_POST.Match(html);
            //				while (m.Success) {
            //					String ss = m.Groups [1].Value;
            //
            //					var pp = new ExportPreviewPost();
            //					pp.id = SIMILAR_POST_ID.FirstString (ss);
            //					pp.image = SIMILAR_POST_IMAGE.FirstString (ss);
            //
            //					{
            //						var s1 = SIMILAR_POST_TITLE2.Matches (ss).Cast<Match>().Select(s=>s.Groups[1].Value).Aggregate("",(a,s)=>a+", "+s).Trim();
            //						var s2 = SIMILAR_POST_TITLE3.Matches (ss).Cast<Match>().Select(s=>s.Groups[1].Value).Aggregate("",(a,s)=>a+", "+s).Trim();
            //
            //						if (s1 == "" && s2 == "") pp.title = null;
            //						else if (s1 == "") pp.title = s2;
            //						else if (s2 == "") pp.title = s1;
            //						else pp.title = s1 + ", " + s2;
            //					}
            //					if (pp.title == null) pp.title = SIMILAR_POST_TITLE.FirstString (ss);
            //
            //					callback(new PostExportState{State = PostExportState.ExportState.LinkedPost, LinkedPost = pp});
            //				}
            //			}
        }

        #endregion

        #region Private methods

        private int readTag(String html, int position)
        {
            int level = 0;
            do
            {
                int i = html.IndexOf('<', position);
                int endTag = html.IndexOf('>', i + 1);

                if (SINGLE_TAGS.Any(s => s == html.Substring(i, s.Length)))
                {
                    position = html.IndexOf('>', i);
                    if (position < 0) throw new Exception();
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

        private ExportComment getComment(String html, int start, int end)
        {
            String s = html.Substring(start, end + 1 - start);
            var c = new ExportComment();

            c.id = COMMENT_ID.FirstString(s);
            c.text = TEXT.FirstString(s);
            c.created = TIMESTAMP.FirstLong(s) * 1000L;

            c.userName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(s))).Replace('+', ' ');
            c.userImage = "http://img0.joyreactor.cc/pics/avatar/user/" + USER_ID.FirstString(s);

            c.rating = COMMENT_RATING.FirstFloat(s, CultureInfo.InvariantCulture);

            var m = COMMENT_IMAGES.Match(s);
            if (m.Success)
            {
                var u = m.Groups[1].Value + m.Groups[2].Value;
                c.attachments = new[] { new ExportComment.ExportAttachment { imageUrl = u } };
            }
            else
            {
                c.attachments = ExportComment.EmptyAttachments;
            }

            return c;
        }

        private int readChildComments(String html, int position, String parentId, Action<ExportComment> callback)
        {
            int end;
            int initPosition = position;
            while (true)
            {
                end = readTag(html, position);
                if (end < 0) break;

                // Оптимизированная (по памяти и CPU) проверка случая когда "нет комментариев"
                if (parentId == null && position == initPosition)
                {
                    if (end - position < 100 && html.Substring(position, end - position).Contains("нет комментариев")) return position;
                }

                var c = getComment(html, position, end);
                c.parentId = parentId;
                callback(c);

                end = skipHtmlTag(html, end + 1);
                end = readChildComments(html, end, c.id, callback);
                position = skipHtmlTag(html, end);
            }
            return position;
        }

        private int skipHtmlTag(string html, int position)
        {
            return html.IndexOf('>', position) + 1;
        }

        private void ExtractPostCoollection(ID.TagType type, string value, int lastLoadedPage, IDictionary<string, string> cookies, Action<CollectionExportState> callback)
        {
            cookies.Add("showVideoGif2", "1");
            var html = downloader.GetText(
                GenerateUrl(type, value, lastLoadedPage),
                new RequestParams { Cookies = cookies });
            callback(new CollectionExportState { State = CollectionExportState.ExportState.Begin });

            var s = new ExportTag();
            s.image = SUB_POSTER.FirstString(html); ;
            s.firstPage = CURRENT_PAGE.FirstInt(html);
            callback(new CollectionExportState { State = CollectionExportState.ExportState.TagInfo, TagInfo = s });

            var m = POST.Match(html);
            if (m.Success)
            {
                do
                {
                    ExportPost p = CreatePost(m.Groups[1].Value);
                    callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });

                    m = m.NextMatch();
                } while (m.Success);
            }
            else
            {
                m = POST_AUTHORIZED.Match(html);
                while (m.Success)
                {
                    var p = CreatePost(m.Groups[1].Value);
                    callback(new CollectionExportState { State = CollectionExportState.ExportState.PostItem, Post = p });

                    m = m.NextMatch();
                }
            }

            if (lastLoadedPage == 0)
            {
                m = SUB_LINKED_SUBS.Match(html);
                while (m.Success)
                {
                    var t = new ExportLinkedTag();
                    t.name = WebUtility.HtmlDecode(m.Groups[2].Value);
                    t.group = "None";
                    t.image = m.Groups[1].Value;
                    t.value = Uri.UnescapeDataString(Uri.UnescapeDataString(m.Groups[3].Value));
                    callback(new CollectionExportState { State = CollectionExportState.ExportState.LikendTag, LinkedTag = t });

                    m = m.NextMatch();
                }
            }
        }

        private Uri GenerateUrl(ID.TagType type, string tag, int beforeLoadedPage)
        {
            StringBuilder url = new StringBuilder("http://joyreactor.cc");
            if (type == ID.TagType.Favorite)
            {
                url.Append("/user/").Append(Uri.EscapeDataString(tag)).Append("/favorite");
            }
            else
            {
                if (tag != null) url.Append("/tag/").Append(Uri.EscapeUriString(tag));
                if (ID.TagType.Best == type) url.Append("/best");
                else if (ID.TagType.All == type) url.Append(tag == null ? "/all" : "/new");
            }

            if (beforeLoadedPage > 0) url.Append("/").Append(beforeLoadedPage - 1);
            if ("" + url == "http://joyreactor.cc") url.Append("/");
            return new Uri("" + url);
        }

        private ExportPost CreatePost(string html)
        {
            var p = new ExportPost();

            var m = IMAGE.Match(html);
            if (m.Success)
            {
                p.image = m.Groups[1].Value;
                p.imageWidth = int.Parse(m.Groups[2].Value);
                p.imageHeight = int.Parse(m.Groups[3].Value);
            }
            if (p.image == null)
            {
                m = IMAGE_GIF.Match(html);
                if (m.Success)
                {
                    p.image = m.Groups[1].Value;
                    p.imageWidth = int.Parse(m.Groups[2].Value);
                    p.imageHeight = int.Parse(m.Groups[3].Value);
                }
            }
            if (p.image == null)
            {
                m = IMAGE_BIG.Match(html);
                if (m.Success)
                {
                    p.image = m.Groups[1].Value;
                    p.imageWidth = int.Parse(m.Groups[2].Value);
                    p.imageHeight = int.Parse(m.Groups[3].Value);
                }
            }
            if (p.image == null)
            {
                p.image = ImageFromSharing.FirstString(html);
                if (p.image != null)
                { // XXX Проверить нужно ли ставить фейковые размеры картинки и какое конкретно число
                    p.imageWidth = 512;
                    p.imageHeight = 512;
                }
            }
            if (p.image != null)
            {
                p.image = Regex.Replace(p.image, "/pics/post/full/[\\w\\s%-]+-(\\d+\\.[\\d\\w]+)", "/pics/post/full/-$1");
                p.image = Regex.Replace(p.image, "/pics/post/[\\w\\s%-]+-(\\d+\\.[\\d\\w]+)", "/pics/post/full/-$1");
            }

            p.userName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(html))).Replace('+', ' ');
            p.userImage = USER_IMAGE.FirstString(html);

            p.title = TITLE.FirstString(html);
            if (string.IsNullOrEmpty(p.title)) p.title = null;

            p.id = POST_ID.FirstString(html);
            p.created = CREATED.FirstLong(html) * 1000L;
            p.rating = RATING.FirstFloat(html, CultureInfo.InvariantCulture);

            m = COUB.Match(html);
            if (m.Success)
            {
                p.coub = m.Groups[1].Value;
                p.imageWidth = int.Parse(m.Groups[2].Value);
                p.imageHeight = int.Parse(m.Groups[2].Value);
            }

            return p;
        }

        #endregion
    }
}