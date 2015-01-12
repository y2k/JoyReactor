using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Web
{
    public interface IWebDownloader
	{
        Task<WebResponse> ExecuteAsync(Uri uri, RequestParams reqParams = null);
    }

    public class WebResponse : IDisposable
    {
        public IDictionary<string, string> Cookies { get; set; }
        public Stream Data { get; set; }

        public void Dispose()
        {
            Data?.Dispose();
        }
    }

	public class RequestParams 
	{
		public IDictionary<string, string> Form { get; set; }
		public IDictionary<string, string> Cookies { get; set; }
        public IDictionary<string, string> AdditionHeaders { get; set; }

        public Uri Referer { get; set; }
        public bool UseForeignProxy { get; set; }
    }
}