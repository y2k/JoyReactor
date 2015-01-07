using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Messages;
using JoyReactor.Core.Model.Profiles;

namespace JoyReactor.Core.Model
{
	class AuthStorage : ProfileService.IAuthStorage, ReactorMessageParser.IAuthStorage
	{
		SQLiteConnection db = ServiceLocator.Current.GetInstance<SQLiteConnection> ();

		public Task SaveCookieToDatabase (string username, IDictionary<string, string> cookies)
		{
			return db.RunInTransactionAsync (() => db.Insert (new Profile {
				Cookie = SerializeObject (cookies),
				Site = "" + ID.SiteParser.JoyReactor,
				Username = username
			}));
		}

		string SerializeObject (IDictionary<string, string> o)
		{
			return o.Aggregate ("", (a, s) => a + (a.Length > 0 ? ";" : "") + s.Key + "=" + s.Value);
		}

		public Task<IDictionary<string, string>> GetCookiesAsync ()
		{
//			return Task.Run (() => db
//				.SafeQuery ("SELECT * FROM profiles WHERE Site = ?", "" + ID.SiteParser.JoyReactor)
//				.Select (s => new Dictionary<string,string> ())
//				.First ());

			var cookie = await db.ExecuteScalarAsync<string> (
				"SELECT Cookie FROM profiles WHERE Site = ?", "" + ID.SiteParser.JoyReactor);

//			var profile = await db
//				.QueryAsync ("SELECT * FROM profiles WHERE Site = ?", "" + ID.SiteParser.JoyReactor)
//				.ContinueWith(s=>new Dictionary<string,string>()
//				;
		}
	}
}