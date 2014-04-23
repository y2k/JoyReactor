using System;
using SQLite.Net.Interop;

namespace JoyReactor.Core.Model.Database
{
	public interface ISQLitePlatfromGetter
	{
		ISQLitePlatform GetPlatform();

		string CreatePath(string databaseName);
	}
}