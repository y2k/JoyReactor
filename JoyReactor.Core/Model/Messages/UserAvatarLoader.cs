using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using Refractored.Xam.Settings;
using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Messages
{
    class UserAvatarLoader
    {
        IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
        AvatarCache cache = new AvatarCache();
        List<MessageFetcher.RawMessage> messages;

        public UserAvatarLoader(List<MessageFetcher.RawMessage> messages)
        {
            this.messages = messages;
        }

        internal async Task LoadAsync()
        {
            foreach (var message in messages)
            {
                var avatar = cache.Get(message.UserName);
                if (avatar == null)
                {
                    avatar = await GetFromWeb(message.UserName);
                    cache.Put(message.UserName, avatar);
                }
                message.UserImage = avatar;
            }
        }

        async Task<string> GetFromWeb(string userName)
        {
            var html = await downloader.GetTextAsync(new Uri("http://joyreactor.cc/user/" + userName));
            return Regex.Match(html, "http://[^/]+/pics/avatar/user/\\d+").Value;
        }

        class AvatarCache
        {
            internal string Get(string key)
            {
                return CrossSettings.Current.GetValueOrDefault<string>(GetFullKey(key));
            }

            internal void Put(string key, string value)
            {
                CrossSettings.Current.AddOrUpdateValue(GetFullKey(key), value);
            }

            string GetFullKey(string key)
            {
                return "avatar-cache_" + key;
            }
        }
    }
}