using Microsoft.Practices.ServiceLocation;
using PCLStorage;
using SQLite.Net;
using SQLite.Net.Interop;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    public class SQLiteConnectionFactory
    {
        const string DatabaseName = "net.itwister.joyreactor.main.db";
        const int DatabaseVersion = 1;

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
                        InitializeDatabase(localInstance);
                        Instance = localInstance;
                    }
                }
            }
            return Instance;
        }

        internal static async void InitializeDatabase(AsyncSQLiteConnection db)
        {
            int ver = await GetUserVesion(db);
            if (ver == 0)
            {
                OnCreate(db);
                await SetUserVersion(db, DatabaseVersion);
            }
            else if (ver < DatabaseVersion)
            {
                OnUpdate(ver, DatabaseVersion);
                await SetUserVersion(db, DatabaseVersion);
            }
        }

        static async void OnCreate(AsyncSQLiteConnection db)
        {
            await new CreateTablesTransaction(db).Execute();
            new CreateDefaultTagsTransaction(db).Execute();
        }

        protected static void OnUpdate(int oldVersion, int newVersion)
        {
            // Reserverd
        }

        static Task SetUserVersion(AsyncSQLiteConnection db, int version)
        {
            return db.ExecuteAsync("PRAGMA user_version = " + version);
        }

        static Task<int> GetUserVesion(AsyncSQLiteConnection db)
        {
            return db.ExecuteScalarAsync<int>("PRAGMA user_version");
        }
    }
}