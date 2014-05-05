using System;
using JoyReactor.Core.Model.Database;
using SQLite.Net.Platform.WindowsPhone8;
using System.IO;
using System.IO.IsolatedStorage;
using Windows.Storage;
using System.Windows;

namespace JoyReactor.WP.Model
{
    public class Wp8SQLitePlatformGetter : ISQLitePlatfromGetter
    {
        #region ISQLitePlatfromGetter implementation

        public SQLite.Net.Interop.ISQLitePlatform GetPlatform()
        {
            return new SQLitePlatformWP8();
        }

        public string CreatePath(string databaseName)
        {
            return ":memory:";

            //InitializeDatabaseFile(databaseName);
            //return Path.Combine(ApplicationData.Current.LocalFolder.Path, databaseName);

            var s = Path.Combine(ApplicationData.Current.LocalFolder.Path, databaseName);
            if (!File.Exists(s))
            {
                var buf = new byte[5120];
                using (var i = Application.GetResourceStream(new Uri("Assets/stub.db", UriKind.Relative)).Stream)
                {
                    using (var o = File.Open(s, FileMode.Create))
                    {
                        int count;
                        while ((count = i.Read(buf, 0, buf.Length)) > 0) o.Write(buf, 0, count);
                    }
                }
            }
            return s;
        }

        #endregion

        //private void InitializeDatabaseFile(string filename)
        //{
        //    var isf = IsolatedStorageFile.GetUserStoreForApplication();
        //    if (isf.FileExists(filename)) return;

        //    var buf = new byte[5120];
        //    using (var i = Application.GetResourceStream(new Uri("Assets/stub.db", UriKind.Relative)).Stream)
        //    {
        //        using (var o = new IsolatedStorageFileStream(filename, FileMode.Create, FileAccess.Write, isf))
        //        {
        //            int count;
        //            while ((count = i.Read(buf, 0, buf.Length)) > 0) o.Write(buf, 0, count);
        //        }
        //    }
        //}
    }
}