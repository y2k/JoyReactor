using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Messages;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class AuthRepository : ProfileService.IAuthStorage, ReactorMessageParser.IAuthStorage, JoyReactorProvider.IAuthStorage
    {
        SQLiteConnection db = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task ClearDatabase()
        {
            return db.RunInTransactionAsync(() =>
            {
                db.SafeExecute("DELETE FROM posts");
                db.SafeExecute("DELETE FROM tag_post");
                db.SafeExecute("DELETE FROM tags WHERE Flags & ? != 0", Tag.FlagWebRead);
                db.SafeExecute("DELETE FROM profiles");
            });
        }

        string SerializeObject(IDictionary<string, string> cookies)
        {
            return cookies.Aggregate("", (a, s) => a + (a.Length > 0 ? ";" : "") + s.Key + "=" + s.Value);
        }

        public async Task<IDictionary<string, string>> GetCookiesAsync()
        {
            var cookies = await db.ExecuteScalarAsync<string>("SELECT Cookie FROM profiles LIMIT 1");
            if (cookies == null)
                return new Dictionary<string, string>();
            return DeserializeCookies(cookies);
        }

        IDictionary<string, string> DeserializeCookies(string flatCookies)
        {
            return flatCookies
                .Split(';')
                .Select(s => s.Split('='))
                .ToDictionary(s => s[0], s => s[1]);
        }

        public Task<string> GetCurrentUserNameAsync()
        {
            return db.ExecuteScalarAsync<string>(
                "SELECT Username FROM profiles LIMIT 1",
                "" + ID.SiteParser.JoyReactor);
        }

        public async Task<Profile> GetCurrentProfileAsync()
        {
            return (await db.QueryAsync<Profile>("SELECT * FROM profiles LIMIT 1")).First();
        }

        public Task SaveCookieToDatabaseAsync(string username, IDictionary<string, string> cookies)
        {
            return db.InsertAsync(CreateProfile(username, cookies));
        }

        Profile CreateProfile(string username, IDictionary<string, string> cookies)
        {
            return new Profile
            {
                Cookie = SerializeObject(cookies),
                UserName = username
            };
        }
    }
}