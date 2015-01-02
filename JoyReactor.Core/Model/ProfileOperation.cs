using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Model
{
	public class ProfileOperation
	{
		public async Task LoginAsync (string username, string password)
		{
			var cookies = await GetParser ().LoginAsync (username, password);
			if (cookies == null || cookies.Count < 1)
				throw new Exception ("Can't login as " + username);

			await SaveCookieToDatabase (username, cookies);
			await SyncListOfMyTagsWithWeb ();
		}

		Task SaveCookieToDatabase (string username, IDictionary<string, string> c)
		{
			return GetDB ().RunInTransactionAsync (() => {
				ClearDatabaseFromOldData ();
				GetDB ().SafeInsert (new Profile {
					Cookie = SerializeObject (c),
					Site = "" + ID.SiteParser.JoyReactor,
					Username = username
				});
			});
		}

		Task SyncListOfMyTagsWithWeb ()
		{
			return new MyProfileInformation ().LoadAsync ();
		}

		public Task LogoutAsync ()
		{
			return GetDB ().RunInTransactionAsync (ClearDatabaseFromOldData);
		}

		void ClearDatabaseFromOldData ()
		{
			GetDB ().SafeExecute ("DELETE FROM posts");
			GetDB ().SafeExecute ("DELETE FROM tag_post");
			GetDB ().SafeExecute ("DELETE FROM tags WHERE Flags & ? != 0", Tag.FlagWebRead);
			GetDB ().SafeExecute ("DELETE FROM profiles");
		}

		SiteParser GetParser ()
		{
			return ServiceLocator.Current
				.GetInstance<SiteParser[]> ()
				.First (s => s.ParserId == ID.SiteParser.JoyReactor);
		}

		static SQLiteConnection GetDB ()
		{
			return ServiceLocator.Current.GetInstance<SQLiteConnection> ();
		}

		static string SerializeObject (IDictionary<string, string> o)
		{
			return o.Aggregate ("", (a, s) => a + (a.Length > 0 ? ";" : "") + s.Key + "=" + s.Value);
		}

		static IDictionary<string, string> DeserializeObject (string o)
		{
			return o.Split (';').Select (s => s.Split ('=')).ToDictionary (s => s [0], s => s [1]);
		}
	}
}