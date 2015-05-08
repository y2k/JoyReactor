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
        public static SQLiteConnection Create()
        {
            return SQLiteConnectionFactory.CreateForTests(GetPlatfrom());
        }

        static ISQLitePlatform GetPlatfrom()
        {
            switch (Environment.OSVersion.Platform)
            {
                case PlatformID.Unix:
                    return new SQLitePlatformGeneric();
                case PlatformID.Win32Windows:
                case PlatformID.Win32NT:
                    return new SQLitePlatformWin32();
            }
            throw new Exception("Not supported OS: " + Environment.OSVersion.Platform);
        }
    }
}