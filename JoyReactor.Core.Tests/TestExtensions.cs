using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Tests.Helpers;
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

        public static int CreatePostIdDatabase(ID.SiteParser parser, string postId)
        {
            var post = new Post { PostId = parser + "-" + postId };
            return GetDatabase().Insert(post);
        }

        public static void SetUp()
        {
            var provider = new DefaultServiceLocator(new TestModule());
            ServiceLocator.SetLocatorProvider(() => provider);
        }

        public static void TearDown(object testInstance)
        {
            Messenger.Default.Unregister(testInstance);
        }

        public static SQLiteConnection GetDatabase()
        {
            return ServiceLocator.Current.GetInstance<SQLiteConnection>();
        }
    }
}