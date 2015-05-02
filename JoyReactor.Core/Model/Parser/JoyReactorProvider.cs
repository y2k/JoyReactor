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
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Parser
{
    public class JoyReactorProvider
    {
        #region New instance factory

        JoyReactorProvider() { }

        public static JoyReactorProvider Create()
        {
            return new JoyReactorProvider();
        }

        #endregion

        public Task LoadTagAndPostListAsync(ID id, IListStorage listStorage, bool isFirstPage)
        {
            return new TagProvider(id, listStorage, isFirstPage).ComputeAsync();
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

        public interface IListStorage
        {
            Task AddPost(Post post);

            Task CommitAsync();
        }

        public interface IStorage
        {
            Task SaveNewOrUpdatePostAsync(Post post);

            Task UpdateTagInformationAsync(ID id, string image, int nextPage, bool hasNextPage);

            Task ReplacePostAttachments(string postId, List<Attachment> attachments);

            Task RemovePostComments(string postId);

            Task SaveNewPostCommentAsync(string postId, int parrentCommentId, Comment comment, string[] attachments);

            Task SaveNewOrUpdateProfileAsync(Profile profile);

            Task ReplaceCurrentUserReadingTagsAsync(IEnumerable<string> readingTags);

            Task<int> GetNextPageForTagAsync(ID id);

            Task SaveRelatedPostsAsync(string postId, List<RelatedPost> posts);

            Task SaveLinkedTagsAsync(ID id, string groupName, ICollection<Tag> tags);

            Task RemoveLinkedTagAsync(ID id);
        }

        public interface IAuthStorage
        {
            Task<string> GetCurrentUserNameAsync();

            Task<IDictionary<string, string>> GetCookiesAsync();

            Task SaveCookieToDatabaseAsync(string username, IDictionary<string, string> cookies);
        }
    }
}