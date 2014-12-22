using JoyReactor.Core.Model.Database;
using SQLite.Net;
using SQLite.Net.Platform.Win32;

namespace JoyReactor.Core.Tests.Helpers
{
    public class MockSQLiteConnection
    {
        public static SQLiteConnection Create()
        {
            var db = new SQLiteConnection(new SQLitePlatformWin32(), ":memory:");
            MainDb.InitializeDatabase(db);
            return db;
        }
    }
}