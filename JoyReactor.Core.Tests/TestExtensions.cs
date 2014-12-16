using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Tests.Inner;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;

namespace JoyReactor.Core.Tests
{
    static class TestExtensions
    {
        public static void SetFakeSite(string url, string filename)
        {
            var downloader = (MockWebDownloader)ServiceLocator.Current.GetInstance<IWebDownloader>();
            downloader.RouteUrls[url] = filename;
        }

        public static void SetUp()
        {
            var provider = new DefaultServiceLocator(new TestModule());
            ServiceLocator.SetLocatorProvider(() => provider);
        }

        public static SQLiteConnection GetDatabase()
        {
            return ServiceLocator.Current.GetInstance<SQLiteConnection>();
        }
    }
}