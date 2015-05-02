using Microsoft.Practices.ServiceLocation;
using PCLStorage;
using SQLite.Net;
using SQLite.Net.Interop;
using System;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    public class SQLiteConnectionFactory
    {
        const string DatabaseName = "net.itwister.joyreactor.main.db";

        static Lazy<AsyncSQLiteConnection> LazyInstance
            = new Lazy<AsyncSQLiteConnection>(
                () =>
                {
                    var path = PortablePath.Combine(FileSystem.Current.LocalStorage.Path, DatabaseName);
                    var platform = ServiceLocator.Current.GetInstance<ISQLitePlatform>();
                    var result = new AsyncSQLiteConnection(new SQLiteConnection(platform, path));
                    // TODO: убрать блокировку потока
                    Task.Run(() => new InitializeTransaction(result).Execute()).Wait();
                    return result;
                });

        internal static AsyncSQLiteConnection Create()
        {
            return LazyInstance.Value;
        }
    }
}