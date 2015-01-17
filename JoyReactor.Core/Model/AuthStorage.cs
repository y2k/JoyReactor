using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Messages;
using JoyReactor.Core.Model.Profiles;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Model
{
    class AuthStorage : ProfileService.IAuthStorage, ReactorMessageParser.IAuthStorage, ReactorParser.IAuthStorage
    {
        SQLiteConnection db = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task SaveCookieToDatabase(string username, IDictionary<string, string> cookies)
        {
            return db.InsertAsync(CreateProfile(username, cookies));
        }

        Profile CreateProfile(string username, IDictionary<string, string> cookies)
        {
            return new Profile
            {
                Cookie = SerializeObject(cookies),
                Site = "" + ID.SiteParser.JoyReactor,
                Username = username
            };
        }

        string SerializeObject(IDictionary<string, string> cookies)
        {
            return cookies.Aggregate("", (a, s) => a + (a.Length > 0 ? ";" : "") + s.Key + "=" + s.Value);
        }

        public async Task<IDictionary<string, string>> GetCookiesAsync()
        {
            var cookies = await db.ExecuteScalarAsync<string>(
                     "SELECT Cookie FROM profiles WHERE Site = ?", 
                     "" + ID.SiteParser.JoyReactor);
            if (cookies == null)
                return new Dictionary<string, string>();
            return DeserializeCookies(cookies);
        }

        IDictionary<string,string> DeserializeCookies(string flatCookies)
        {
            return flatCookies
				.Split(';')
				.Select(s => s.Split('='))
				.ToDictionary(s => s[0], s => s[1]);
        }

        public Task<string> GetCurrentUserNameAsync()
        {
            return db.ExecuteScalarAsync<string>(
                "SELECT Username FROM profiles WHERE Site = ?", 
                "" + ID.SiteParser.JoyReactor);
        }
    }
}