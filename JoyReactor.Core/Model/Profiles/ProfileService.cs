using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Profiles
{
	class ProfileService : IProfileService
	{
		IAuthStorage storage = new AuthStorage ();

		public async Task<MyProfile> GetMyProfile ()
		{
			var loader = new MyProfileLoader ();
			await loader.LoadAsync ();
			if (!loader.IsValid)
				throw new NotLogedException ();
			return new MyProfile { Username = loader.Username, Rating = loader.Rating };
		}

		#region Login

		public async Task Login (string username, string password)
		{
			var cookies = await GetParser ().LoginAsync (username, password);
			if (cookies == null || cookies.Count < 1)
				throw new Exception ("Can't login as " + username);

			await storage.SaveCookieToDatabase (username, cookies);
			await SyncListOfMyTagsWithWeb ();
		}

		SiteParser GetParser ()
		{
			return ServiceLocator.Current
                .GetInstance<SiteParser[]> ()
                .First (s => s.ParserId == ID.SiteParser.JoyReactor);
		}

		//		Task SaveCookieToDatabase (string username, IDictionary<string, string> c)
		//		{
		//			return GetDB ().RunInTransactionAsync (() => {
		//				ClearDatabaseFromOldData ();
		//				GetDB ().SafeInsert (new Profile {
		//					Cookie = SerializeObject (c),
		//					Site = "" + ID.SiteParser.JoyReactor,
		//					Username = username
		//				});
		//			});
		//		}
		//
		//		static string SerializeObject (IDictionary<string, string> o)
		//		{
		//			return o.Aggregate ("", (a, s) => a + (a.Length > 0 ? ";" : "") + s.Key + "=" + s.Value);
		//		}

		async Task SyncListOfMyTagsWithWeb ()
		{
			await new MyProfileLoader ().LoadAsync ();
			await InvaliteTagList ();
		}

		#endregion

		public async Task Logout ()
		{
			await GetDB ().RunInTransactionAsync (ClearDatabaseFromOldData);
			await InvaliteTagList ();
		}

		SQLiteConnection GetDB ()
		{
			return ServiceLocator.Current.GetInstance<SQLiteConnection> ();
		}

		Task InvaliteTagList ()
		{
			return Task.Run (() => TagCollectionModel.OnInvalidateEvent ());
		}

		void ClearDatabaseFromOldData ()
		{
			GetDB ().SafeExecute ("DELETE FROM posts");
			GetDB ().SafeExecute ("DELETE FROM tag_post");
			GetDB ().SafeExecute ("DELETE FROM tags WHERE Flags & ? != 0", Tag.FlagWebRead);
			GetDB ().SafeExecute ("DELETE FROM profiles");
		}

		internal interface IAuthStorage
		{

			Task SaveCookieToDatabase (string username, IDictionary<string, string> cookies);
		}
	}
}