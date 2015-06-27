using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Common;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Parser
{
    public class ProfileRequest
    {
        WebDownloader downloader = ServiceLocator.Current.GetInstance<WebDownloader>();
        Storage storage;

        public ProfileRequest(Storage storage)
        {
            this.storage = storage;
        }

        public async Task ComputeAsync()
        {
            var username = await GetCurrentUserNameAsync();
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
                var readingTags = div
                    .Descendants("a")
                    .Select(s => profileTagRx.FirstString(s.GetHref()))
                    .Where(s => s != null)
                    .Select(s => UnescapeTagName(s))
                    .Select(s => new Tag { Title = s, TagId = ID.Factory.NewTag(s.ToLower()).SerializeToString() })
                    .ToList();
                await storage.ReplaceCurrentUserReadingTagsAsync(readingTags);
            }

            await storage.SaveNewOrUpdateProfileAsync(p);
        }

        async Task<string> GetCurrentUserNameAsync()
        {
            var profiles = await new ProfileRepository().GetAllAsync();
            return profiles.FirstOrDefault()?.UserName;
        }

        static string UnescapeTagName(string tag)
        {
            return tag
                .UnescapeDataString()
                .UnescapeDataString()
                .Replace('+', ' ');
        }

        public interface Storage
        {
            Task ReplaceCurrentUserReadingTagsAsync(IEnumerable<Tag> readingTags);

            Task SaveNewOrUpdateProfileAsync(Profile profile);
        }
    }
}