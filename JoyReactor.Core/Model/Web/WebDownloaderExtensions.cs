using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using System.Xml.Linq;
using HtmlAgilityPack;

namespace JoyReactor.Core.Model.Web
{
    static class WebDownloaderExtensions
    {
        public static async Task<XDocument> GetXmlAsync(this WebDownloader instance, Uri uri)
        {
            var text = await instance.GetTextAsync(uri);
            var doc = await Task.Run(() => XDocument.Load(new StringReader(text)));
            return doc;
        }

        public static async Task<HtmlDocument> GetDocumentAsync(this WebDownloader instance, Uri uri, RequestParams reqParams = null)
        {
            using (var response = await instance.ExecuteAsync(uri, reqParams))
            {
                var doc = new HtmlDocument();
                await Task.Run(() => doc.Load(response.Stream));
                return doc;
            }
        }

        public static async Task PostAsync(this WebDownloader instance, Uri uri, RequestParams requestParams)
        {
            using (var response = await instance.ExecuteAsync(uri, requestParams))
            {
            }
        }

        public static async Task<IDictionary<string, string>> PostForCookiesAsync(this WebDownloader instance, Uri uri, RequestParams reqParams = null)
        {
            if (reqParams == null || reqParams.Form == null)
                throw new Exception();
            using (var response = await instance.ExecuteAsync(uri, reqParams))
                return response.Cookies;
        }

        public static async Task<string> GetTextAsync(this WebDownloader instance, Uri uri, RequestParams reqParams = null)
        {
            using (var response = await instance.ExecuteAsync(uri, reqParams))
                return await new StreamReader(response.Stream).ReadToEndAsync();
        }
    }
}