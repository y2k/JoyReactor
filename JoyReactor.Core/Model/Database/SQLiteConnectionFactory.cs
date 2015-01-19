using Microsoft.Practices.ServiceLocation;
using PCLStorage;
using SQLite.Net;
using SQLite.Net.Interop;
using System;

namespace JoyReactor.Core.Model.Database
{
    public class SQLiteConnectionFactory
	{
		const string DatabaseName = "net.itwister.joyreactor.main.db";
		const int DatabaseVersion = 1;

		static volatile SQLiteConnection Instance;
		static object syncRoot = new object ();

        public static SQLiteConnection Create()
        {
            if (Instance == null)
            {
                lock (syncRoot)
                {
                    if (Instance == null)
                    {
                        var path = PortablePath.Combine(FileSystem.Current.LocalStorage.Path, DatabaseName);
                        var platform = ServiceLocator.Current.GetInstance<ISQLitePlatform>();
                        var localInstance = new SQLiteConnection(platform, path);
                        InitializeDatabase(localInstance);
                        Instance = localInstance;
                    }
                }
            }
            return Instance;
        }

		public static void InitializeDatabase (SQLiteConnection db)
		{
			int ver = GetUserVesion (db);
			if (ver == 0)
				db.RunInTransaction (() => {
					OnCreate (db);
					SetUserVersion (db, DatabaseVersion);
				});
			else if (ver < DatabaseVersion)
				db.RunInTransaction (() => {
					OnUpdate (ver, DatabaseVersion);
					SetUserVersion (db, DatabaseVersion);
				});
		}

		static void OnCreate (SQLiteConnection db)
		{
            new CreateTablesTransaction(db).Execute();
            new CreateDefaultTagsTransaction (db).Execute ();
		}

		protected static void OnUpdate (int oldVersion, int newVersion)
		{
			// Reserverd
		}

		static void SetUserVersion (SQLiteConnection db, int version)
		{
			db.Execute ("PRAGMA user_version = " + version);
		}

		static int GetUserVesion (SQLiteConnection db)
		{
			return db.ExecuteScalar<int> ("PRAGMA user_version");
		}
	}
}