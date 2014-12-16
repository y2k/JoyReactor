using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Tests.Inner;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Tests
{
    static class TestExtensions
    {
        public static void SetFakeSite(string url, string filename)
        {
            var downloader = (MockWebDownloader)ServiceLocator.Current.GetInstance<IWebDownloader>();
            downloader.RouteUrls[url] = filename;
        }
    }
}