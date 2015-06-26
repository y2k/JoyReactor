using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Common;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Web;

namespace JoyReactor.Core.Model.Parser
{
    class TagImageRequest : ImageLoader<Tag>
    {
        internal TagImageRequest(Tag tag)
            : this(new List<Tag> { tag })
        {
        }

        internal TagImageRequest(List<Tag> tags)
            : base(tags, "tag_image.")
        {
        }

        protected override async Task<string> GetFromWeb(WebDownloader downloader, Tag item)
        {
            var uri = new TagUrlBuilder().Build(ID.DeserializeFromString(item.TagId), 0);
            var html = await downloader.GetTextAsync(uri);
            var match = Regex.Match(html, @"\<img itemprop=""photo"" src=""([^""]+)");
            if (match.Success)
                return match.Groups[1].Value;
            match = Regex.Match(html, @"\<img src=""([^""]+)""[^>]+class=""blog_avatar""");
            if (match.Success)
                return match.Groups[1].Value;
            return null;
        }

        static string GetTagName(Tag item)
        {
            return ID.DeserializeFromString(item.TagId).Tag;
        }

        protected override string GetKey(Tag item)
        {
            return item.Title;
        }

        protected override void Set(Tag item, string value)
        {
            item.BestImage = value;
        }
    }
}