using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using HtmlAgilityPack;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Parser
{
    class LoginRequest
    {
        WebDownloader downloader = ServiceLocator.Current.GetInstance<WebDownloader>();
        IProviderAuthStorage authStorage = ServiceLocator.Current.GetInstance<IProviderAuthStorage>();

        string username;
        string password;

        internal LoginRequest(string username, string password)
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
                    NotFollowRedirects = true,
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

            if (!hs.ContainsKey("joyreactor_sess"))
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
}
