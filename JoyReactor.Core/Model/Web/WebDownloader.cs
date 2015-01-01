using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using HtmlAgilityPack;

namespace JoyReactor.Core.Model.Web
{
    public class WebDownloader : IWebDownloader
    {
        #region Constants

        const string UserAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68";
        const string Accept = "text/html";

        #endregion

        static readonly CookieContainer DefaultCookies = new CookieContainer();
        static readonly Lazy<HttpClient> DefaultClient = new Lazy<HttpClient>(() =>
        {
            var handler = new HttpClientHandler
            {
                UseCookies = true,
                CookieContainer = DefaultCookies,
            };

            // TODO Вызывает ошибку компилирования в Xamarin Studio
            //if (handler.SupportsAutomaticDecompression)
            //{
            //    handler.AutomaticDecompression = DecompressionMethods.GZip | DecompressionMethods.Deflate;
            //}

            var client = new HttpClient(handler);
            client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
            client.DefaultRequestHeaders.Accept.ParseAdd(Accept);
            return client;
        });

        #region IWebDownloader implementation

        public System.IO.Stream GetResource(Uri uri, RequestParams reqParams = null)
        {
            var r = DefaultClient.Value.GetAsync(uri).Result;
            return r.Content.ReadAsStreamAsync().Result;
        }

        public DocumentReponse GetDocument(Uri uri, RequestParams reqParams = null)
        {
            using (var handler = new HttpClientHandler())
            {
                handler.CookieContainer = new CookieContainer();
                handler.UseCookies = true;
                handler.AllowAutoRedirect = true;

                using (var client = new HttpClient(handler))
                {
                    client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
                    client.DefaultRequestHeaders.Accept.ParseAdd(Accept);

                    var result = new DocumentReponse();
                    using (var r = client.GetAsync(uri).Result)
                    {
                        using (var s = r.Content.ReadAsStreamAsync().Result)
                        {
                            result.Document = new HtmlDocument();
                            result.Document.Load(s);
                        }
                    }

                    var u = new Uri("http://" + (uri.Host.StartsWith("www.") ? "" : "www.") + uri.Host);
                    result.Cookies = handler.CookieContainer.GetCookies(u).Cast<Cookie>().ToDictionary(s => s.Name, s => s.Value);
                    return result;
                }
            }
        }

        public string GetText(Uri uri, RequestParams reqParams = null)
        {
            using (var handler = new HttpClientHandler())
            {
                handler.UseCookies = true;
                handler.CookieContainer = new CookieContainer();

                if (reqParams != null)
                {
                    if (reqParams.UseForeignProxy)
                        handler.Proxy = new DefaultProxy();

                    if (reqParams.Cookies != null)
                        foreach (var k in reqParams.Cookies.Keys)
                            handler.CookieContainer.Add(uri, new Cookie(k, reqParams.Cookies[k]));
                }

                using (var client = new HttpClient(handler))
                {
                    client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
                    client.DefaultRequestHeaders.Accept.ParseAdd(Accept);

                    return client.GetStringAsync(uri).Result;
                }
            }
        }

        public IDictionary<string, string> PostForHeaders(Uri uri, RequestParams reqParams = null)
        {
            throw new NotImplementedException();
        }

        public IDictionary<string, string> PostForCookies(Uri uri, RequestParams reqParams = null)
        {
            if (reqParams == null || reqParams.Form == null) throw new Exception();

            var req = new HttpRequestMessage();
            req.RequestUri = uri;
            req.Method = HttpMethod.Post;
            req.Content = new FormUrlEncodedContent(reqParams.Form);
            if (reqParams.Referer != null) req.Headers.Referrer = reqParams.Referer;

            using (var handler = new HttpClientHandler())
            {
                handler.UseCookies = true;
                handler.CookieContainer = new CookieContainer();

                if (reqParams != null && reqParams.Cookies != null)
                {
                    foreach (var k in reqParams.Cookies.Keys)
                    {
                        handler.CookieContainer.Add(uri, new Cookie(k, reqParams.Cookies[k]));
                    }
                }

                using (var client = new HttpClient(handler))
                {
                    client.DefaultRequestHeaders.UserAgent.ParseAdd(UserAgent);
                    client.DefaultRequestHeaders.Accept.ParseAdd(Accept);

                    using (var r = client.SendAsync(req).Result)
                    {
#if DEBUG
                        var s = r.Content.ReadAsStringAsync().Result;
                        s.ToString();
#endif
                    }
                }

                var u = new Uri("http://" + (uri.Host.StartsWith("www.") ? "" : "www.") + uri.Host);
                return handler.CookieContainer.GetCookies(u).Cast<Cookie>().ToDictionary(s => s.Name, s => s.Value);
            }
        }

        #endregion

        class DefaultProxy : IWebProxy
        {
            public ICredentials Credentials { get; set; }

            public Uri GetProxy(Uri destination)
            {
                return new Uri("http://109.201.140.42:8118/");
            }

            public bool IsBypassed(Uri host)
            {
                return false;
            }
        }
    }
}