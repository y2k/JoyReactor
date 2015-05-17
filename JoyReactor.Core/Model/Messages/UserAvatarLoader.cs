using JoyReactor.Core.Model.Common;
using JoyReactor.Core.Model.Web;
using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Messages
{
    class UserAvatarLoader : ImageLoader<MessageFetcher.RawMessage>
    {
        internal UserAvatarLoader(List<MessageFetcher.RawMessage> items) : base(items, "avatar-cache_") { }

        protected override async Task<string> GetFromWeb(WebDownloader downloader, MessageFetcher.RawMessage item)
        {
            var html = await downloader.GetTextAsync(new Uri("http://joyreactor.cc/user/" + item.UserName));
            return Regex.Match(html, "http://[^/]+/pics/avatar/user/\\d+").Value;
        }

        protected override string GetKey(MessageFetcher.RawMessage item)
        {
            return item.UserName;
        }

        protected override void Set(MessageFetcher.RawMessage item, string value)
        {
            item.UserName = value;
        }
    }
}