using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Parser
{
    public class PostRequest
    {
        WebDownloader downloader = ServiceLocator.Current.GetInstance<WebDownloader>();
        IProviderStorage storage = ServiceLocator.Current.GetInstance<IProviderStorage>();

        string postId;
        string htmlPage;

        public PostRequest(string postId)
        {
            this.postId = postId;
        }

        public async Task ComputeAsync()
        {
            await DownloadHtmlPageAsync();
            await SavePostInformation();
            await SavePostAttachments();
            await SaveRelatedPosts();
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
            p.UserName = USER_NAME.FirstString(htmlPage)?.UnescapeDataString().UnescapeDataString().Replace('+', ' ');

            var USER_IMAGE = new Regex("src=\"([^\"]+)\" class=\"avatar\"");
            p.UserImage = USER_IMAGE.FirstString(htmlPage);

            var TITLE = new Regex("<div class=\"post_content\"><span>([^<]*)</span>", RegexOptions.Singleline);
            p.Title = TITLE.FirstString(htmlPage);
            if (string.IsNullOrWhiteSpace(p.Title))
                p.Title = null;

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

        async Task SaveRelatedPosts()
        {
            var posts = await GetRelatedPosts();
            await storage.SaveRelatedPostsAsync(postId, posts);
        }

        Task<List<RelatedPost>> GetRelatedPosts()
        {
            return Task.Run(() =>
                {
                    return new Regex(@"<td class=""similar_post""><a href=""/post/(\d+)""><img src=""([^""]+)")
                        .Matches(htmlPage)
                        .OfType<Match>()
                        .Select(s => new RelatedPost { Image = s.Groups[2].Value })
                        .ToList();
                });
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

                var SINGLE_TAGS = new [] { "<br>", "<param " };
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

            var TEXT = new Regex("comment_txt_\\d+_\\d+\">\\s*<div>(.*?)</div>", RegexOptions.Singleline);
            c.Text = TEXT.FirstString(s)?.Replace("<br>", Environment.NewLine);

            var TIMESTAMP = new Regex("timestamp=\"(\\d+)");
            c.Created = TIMESTAMP.FirstLong(s) * 1000L;

            var COMMENT_RATING = new Regex("<span\\s*class=\"comment_rating\"\\s*comment_id=\"\\d+\">\\s*<span>—\\s*([\\d\\.]+)</span>", RegexOptions.Singleline);
            c.Rating = COMMENT_RATING.FirstFloat(s, CultureInfo.InvariantCulture);

            var USER_NAME = new Regex("href=\"[^\"]+user/([^\"/]+)\"", RegexOptions.Singleline);
            //                c.UserName = Uri.UnescapeDataString(Uri.UnescapeDataString(USER_NAME.FirstString(s))).Replace('+', ' ');
            c.UserName = USER_NAME.FirstString(s)?.UnescapeDataString().UnescapeDataString().Replace('+', ' ');

            var USER_ID = new Regex("userId=\"(\\d+)\"");
            c.UserImage = "http://img0.joyreactor.cc/pics/avatar/user/" + USER_ID.FirstString(s);

            var COMMENT_IMAGES = new Regex("<img src=\"(http://[^\"]+/pics/comment/)[^\"]+(\\-\\d+\\.[^\"]+)");
            var m = COMMENT_IMAGES.Match(s);
            var attchs = m.Success ? new [] { m.Groups[1].Value + m.Groups[2].Value } : new string[0];

            await storage.SaveNewPostCommentAsync(postId, parentId, c, attchs);

            return c.Id;
        }

        #endregion
    }
}
