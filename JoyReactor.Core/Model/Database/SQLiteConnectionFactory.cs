using Microsoft.Practices.ServiceLocation;
using PCLStorage;
using SQLite.Net;
using SQLite.Net.Interop;

namespace JoyReactor.Core.Model.Database
{
    public class SQLiteConnectionFactory
    {
        const string DatabaseName = "net.itwister.joyreactor.main.db";

        static volatile AsyncSQLiteConnection Instance;
        static object syncRoot = new object();

        internal static AsyncSQLiteConnection Create()
        {
            if (Instance == null)
            {
                lock (syncRoot)
                {
                    if (Instance == null)
                    {
                        var path = PortablePath.Combine(FileSystem.Current.LocalStorage.Path, DatabaseName);
                        var platform = ServiceLocator.Current.GetInstance<ISQLitePlatform>();
                        var localInstance = new AsyncSQLiteConnection(new SQLiteConnection(platform, path));
                        new InitializeTransaction(localInstance).Execute();
                        Instance = localInstance;
                    }
                }
            }
            return Instance;
        }
    }
}