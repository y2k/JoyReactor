using HtmlAgilityPack;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using RawMessage = JoyReactor.Core.Model.Messages.MessageFetcher.RawMessage;

namespace JoyReactor.Core.Model.Messages
{
    public class ReactorMessageParser : MessageFetcher.IMessageParser
    {
        IWebDownloader downloader = ServiceLocator.Current.GetInstance<IWebDownloader>();
        IAuthStorage auth = ServiceLocator.Current.GetInstance<IAuthStorage>();

        public async Task<List<RawMessage>> LoadNextPageAsync(int page)
        {
            var doc = await downloader.GetDocumentAsync(
                GenerateUri(page), 
                new RequestParams { Cookies = await auth.GetCookiesAsync() });
            var messages = doc.DocumentNode
				.Select("div.article")
				.Select(ConvertToMessage)
				.ToList();
            await new UserAvatarLoader(messages).LoadAsync();
            return messages;
        }

        Uri GenerateUri(int page)
        {
            return new Uri("http://joyreactor.cc/private/list/" + (page + 1));
        }

        RawMessage ConvertToMessage(HtmlNode node)
        {
            return new RawMessage
            {
                Message = node.Select("div.mess_text")
                    .Select(s => s.InnerText)
                    .Select(WebUtility.HtmlDecode)
                    .Select(s => s.Trim(' ', '\r', '\n'))
                    .First(),
                Created = node
					.Select("div.mess_date > span")
					.Select(s => s.Attr("data-time"))
					.Select(ConvertToDateTime)
					.First(),
                UserName = node.Select("div.mess_from > a").First().InnerText,
                Mode = GetMessageMode(node),
            };
        }

        DateTime ConvertToDateTime(string unixtime)
        {
            return long.Parse(unixtime).DateTimeFromUnixTimestamp();
        }

        RawMessage.MessageMode GetMessageMode(HtmlNode node)
        {
            return node.Select("div.mess_reply").Any()
				? RawMessage.MessageMode.Inbox
				: RawMessage.MessageMode.Outbox;
        }

        public async Task SendMessageToUser(string username, string message)
        {
            await downloader.PostAsync(
                new Uri("http://joyreactor.cc/private/create"),
                new RequestParams
                {
                    Form = new Dictionary<string, string>
                    {
                        ["username"] = username,
                        ["text"] = message,
                    },
                    AdditionHeaders = new Dictionary<string, string>
                    {
                        ["X-Requested-With"] = "XMLHttpRequest",
                        ["Referer"] = "http://joyreactor.cc/private/list",
                    },
                    Cookies = await auth.GetCookiesAsync(),
                });
        }

        public interface IAuthStorage
        {
            Task<IDictionary<string, string>> GetCookiesAsync();
        }
    }
}