using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Common;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Web;

namespace JoyReactor.Core.Model.Parser
{
    class TagImageProvider : ImageLoader<Tag>
    {
        internal TagImageProvider(Tag tag) : this (new List<Tag> { tag }) {}

        internal TagImageProvider(List<Tag> tags) : base(tags, "tag_image.") { }

        protected override async Task<string> GetFromWeb(WebDownloader downloader, Tag item)
        {
            var html = await downloader.GetTextAsync(new Uri("http://joyreactor.cc/tag/" + Uri.EscapeDataString(GetTagName(item))));
            var match = Regex.Match(html, @"\<img itemprop=""photo"" src=""([^""]+)");
            return match.Success ? match.Groups[1].Value : null;
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