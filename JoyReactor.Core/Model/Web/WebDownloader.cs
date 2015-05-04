using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Web
{
    public class WebDownloader : IWebDownloader
    {
        const string UserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68";
        const string Accept = "text/html";

        const bool AllowProxy = false;
        // TODO: вернуть прокси

        public async Task<WebResponse> ExecuteAsync(Uri uri, RequestParams reqParams = null)
        {
            using (var handler = new HttpClientHandler())
            {
                handler.CookieContainer = new CookieContainer();
                handler.UseCookies = true;
                handler.AllowAutoRedirect = false;

                var req = new HttpRequestMessage { RequestUri = uri };
                if (reqParams != null)
                    ApplyParameters(uri, reqParams, req, handler);

                using (var client = new HttpClient(handler))
                {
                    client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
                    client.DefaultRequestHeaders.Accept.ParseAdd(Accept);

                    try
                    {
                        var response = await client.SendAsync(req);
                        var result = new WebResponse
                        {
                            Data = await response.Content.ReadAsStreamAsync(),
                            Cookies = GetCookies(uri, response),
                        };
                        return result;
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                }
            }
        }

        void ApplyParameters(Uri uri, RequestParams reqParams, HttpRequestMessage req, HttpClientHandler handler)
        {
            if (AllowProxy && reqParams.UseForeignProxy)
                handler.Proxy = new DefaultProxy();

            if (reqParams.Cookies != null)
                foreach (var k in reqParams.Cookies.Keys)
                    handler.CookieContainer.Add(uri, new Cookie(k, reqParams.Cookies[k]));
            if (reqParams.Form != null)
            {
                req.Method = HttpMethod.Post;
                req.Content = new FormUrlEncodedContent(reqParams.Form);
            }
            if (reqParams.Referer != null)
                req.Headers.Referrer = reqParams.Referer;
            if (reqParams.AdditionHeaders != null)
                foreach (var s in reqParams.AdditionHeaders)
                    req.Headers.Add(s.Key, s.Value);
        }

        IDictionary<string, string> GetCookies(Uri uri, HttpResponseMessage response)
        {
            return response.Headers
                .Where(s => s.Key == "Set-Cookie")
                .SelectMany(s => s.Value)
                .Select(s => s.Split(';')[0])
                .ToDictionary(s => s.Split('=')[0], s => s.Split('=')[1]);
        }

        IDictionary<string, string> GetCookies(Uri uri, HttpClientHandler handler)
        {
            var u = new Uri("http://" + (uri.Host.StartsWith("www.") ? "" : "www.") + uri.Host);
            return handler.CookieContainer.GetCookies(u).Cast<Cookie>().ToDictionary(s => s.Name, s => s.Value);
        }

        class DefaultProxy : IWebProxy
        {
            public ICredentials Credentials { get; set; } = new NetworkCredential("joyreactor-dev", "4ECB7AAA-4696-4392-84B7-58EBB95A4F51");

            public Uri GetProxy(Uri destination)
            {
                return new Uri("http://109.201.140.42:8080/");
            }

            public bool IsBypassed(Uri host)
            {
                return false;
            }
        }
    }
}