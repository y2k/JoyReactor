using System;
using JoyReactor.Core.Model.Database;
using SQLite.Net.Platform.XamarinAndroid;
using Android.Content;
using JoyReactor.Android.App;
using System.IO;

namespace JoyReactor.Android.Model
{
	public class AndroidSQLitePlatformGetter : ISQLitePlatfromGetter
	{
		#region ISQLitePlatfromGetter implementation

		public SQLite.Net.Interop.ISQLitePlatform GetPlatform ()
		{
			return new SQLitePlatformAndroid ();
		}

		public string CreatePath (string databaseName)
		{
			return Path.Combine("" + JoyReactor.Android.App.App.Instance.GetExternalFilesDir(null), databaseName);
		}

		#endregion
	}
}