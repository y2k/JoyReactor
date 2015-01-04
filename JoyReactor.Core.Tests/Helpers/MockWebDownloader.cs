﻿using JoyReactor.Core.Model.Web;
using System.Linq;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.Helpers
{
    class MockWebDownloader : IWebDownloader
    {
        internal readonly IDictionary<string, string> RouteUrls = new Dictionary<string, string>()
        {
            ["http://joyreactor.cc/post/1323757"] = "response2.html",
            ["http://joyreactor.cc/post/1382511"] = "response3.html",
            ["http://joyreactor.cc/post/861529"] = "resp4.html",
            ["http://pornreactor.cc/post/1323757"] = "response2.html",
            ["http://pornreactor.cc/post/1382511"] = "response3.html",
            ["http://pornreactor.cc/post/861529"] = "resp4.html",

            ["http://joyreactor.cc/"] = "joyreactor_feed.html",
            ["http://joyreactor.cc/tag/комиксы"] = "joyreactor_comics.html",
            ["http://joyreactor.cc/4313"] = "joyreactor_page4313.html",
            ["http://joyreactor.cc/4312"] = "joyreactor_page4312.html",
            ["http://joyreactor.cc/tag/песочница"] = "joyreactor_pesochnica.html",

            ["https://boards.4chan.org/b/"] = "resp_4chan_b.html",
            ["https://boards.4chan.org/b/2"] = "resp_4chan_b_page2.html",
            ["https://boards.4chan.org/wsg/"] = "resp_4chan_wsg.html",
            ["https://boards.4chan.org/b/thread/572092321"] = "resp_4chan_thread_572092321.html",
            ["https://7chan.org/b/"] = "7chan_b.html",
            ["https://7chan.org/gif/"] = "7chan_gif.html",
            ["https://7chan.org/b/1.html"] = "7chan_b_page2.html",
            ["http://m2-ch.ru/b/"] = "m2-ch_b.html",
            ["http://m2-ch.ru/b/1.html"] = "m2-ch_b_page2.html",
            ["http://m2-ch.ru/media/"] = "m2-ch_media.html",
            ["http://m2-ch.ru/a/res/1755718.html"] = "response1.txt",
            ["https://7chan.org/b/res/722687.html"] = "7chan_722687.html",

            ["http://joyreactor.cc/login"] = "joyreactor_login_get.html",
            ["http://joyreactor.cc/user/mykie78"] = "joyreactor_user_mykie78.html",
        };

        IDictionary<string, string> PostCookies = new Dictionary<string, string>()
        {
            ["http://joyreactor.cc/login"] = "joyreactor=ver3b7f680bd57a4df5fcf550da37f93cd88:c80305a37b42b62ca85a1c8bf77a23cfaa941856;remember=5f513a0567f134f877210952f511702f",
        };

        #region IWebDownloader implementation

        public Task<WebResponse> ExecuteAsync(Uri uri, RequestParams reqParams = null)
        {
            if (reqParams?.Form != null)
            {
                return Task.FromResult(new WebResponse { Cookies = GetCookies(uri) });
            }
            else
            {
                var path = GetPathToFile(uri);
                return Task.FromResult(new WebResponse { Data = File.Open(path, FileMode.Open) });
            }
        }

        private IDictionary<string, string> GetCookies(Uri uri)
        {
            var res = PostCookies["" + uri];
            return res.Split(';').Select(s => s.Split('=')).ToDictionary(s => s[0], s => s[1]);
        }

        public Stream GetResource(Uri uri, RequestParams reqParams = null)
        {
            return File.OpenRead(GetPathToFile(uri));
        }

        public string GetText(Uri uri, RequestParams reqParams = null)
        {
            return File.ReadAllText(GetPathToFile(uri));
        }

        public DocumentReponse GetDocument(Uri uri, RequestParams reqParams = null)
        {
            throw new NotImplementedException();
        }

        public IDictionary<string, string> PostForHeaders(Uri uri, RequestParams reqParams = null)
        {
            throw new NotImplementedException();
        }

        public IDictionary<string, string> PostForCookies(Uri uri, RequestParams reqParams = null)
        {
            throw new NotImplementedException();
        }

        #endregion

        private string GetPathToFile(Uri uri)
        {
            var textUrl = "" + uri;
            string name;
            Assert.IsTrue(RouteUrls.TryGetValue(textUrl, out name), "Can't find path for URL = " + textUrl);
            var path = Path.Combine(@"FakeWebResponses", name);
            Assert.IsTrue(File.Exists(path), "File not found at path " + path);
            return path;
        }
    }
}