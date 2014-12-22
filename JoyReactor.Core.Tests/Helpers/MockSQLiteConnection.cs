using JoyReactor.Core.Model.Database;
using System;
using SQLite.Net;
using SQLite.Net.Platform.Win32;
using SQLite.Net.Interop;
using SQLite.Net.Platform.Generic;

namespace JoyReactor.Core.Tests.Helpers
{
	public static class MockSQLiteConnection
	{
		public static SQLiteConnection Create ()
		{
			var db = new SQLiteConnection (GetPlatfrom (), ":memory:");
			MainDb.InitializeDatabase (db);
			return db;
		}

		static ISQLitePlatform GetPlatfrom ()
		{
			if (Environment.OSVersion.Platform == PlatformID.Unix)
				return new SQLitePlatformGeneric ();
			if (Environment.OSVersion.Platform == PlatformID.Win32Windows)
				return new SQLitePlatformWin32 ();
			throw new Exception ("Not supported platform");
		}
	}
}