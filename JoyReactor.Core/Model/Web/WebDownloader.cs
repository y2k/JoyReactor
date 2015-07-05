using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.IO;

namespace JoyReactor.Core.Model.Web
{
    public class WebDownloader
    {
        protected const string UserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68 JoyReactorClient/1.0";
        protected const string Accept = "text/html";

        const bool AllowProxy = false;

        // TODO: вернуть прокси

        public virtual async Task<WebResponse> ExecuteAsync(Uri uri, RequestParams reqParams = null)
        {
            using (var handler = new HttpClientHandler())
            {
                handler.CookieContainer = new CookieContainer();
                handler.UseCookies = true;
                handler.AutomaticDecompression = DecompressionMethods.GZip;

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
                        return new WebResponse
                        {
                            Stream = await response.Content.ReadAsStreamAsync(),
                            Cookies = GetCookies(response),
                            ResponseUri = req.RequestUri,
                            ContentLength = (int)(response.Content.Headers.ContentLength ?? -1),
                        };
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

            handler.AllowAutoRedirect = !reqParams.NotFollowRedirects;
        }

        IDictionary<string, string> GetCookies(HttpResponseMessage response)
        {
            return response.Headers
                .Where(s => s.Key == "Set-Cookie")
                .SelectMany(s => s.Value)
                .Select(s => s.Split(';')[0])
                .ToDictionary(s => s.Split('=')[0], s => s.Split('=')[1]);
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

    public class WebResponse : IDisposable
    {
        public IDictionary<string, string> Cookies { get; set; }

        public Stream Stream { get; set; }

        public Uri ResponseUri { get; set; }

        public int ContentLength { get; set; }

        public void Dispose()
        {
            Stream?.Dispose();
        }
    }

    public class RequestParams
    {
        public IDictionary<string, string> Form { get; set; }

        public IDictionary<string, string> Cookies { get; set; }

        public IDictionary<string, string> AdditionHeaders { get; set; }

        public bool NotFollowRedirects { get; set; }

        public Uri Referer { get; set; }

        public bool UseForeignProxy { get; set; }
    }
}