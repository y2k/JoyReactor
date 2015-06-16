using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Linq;

namespace JoyReactor.Core.Model.Database
{
    class ProfileRepository : Repository<Profile>
    {
        public Task<List<Profile>> GetAllAsync()
        {
            return Connection.QueryAsync<Profile>("SELECT * FROM profiles");
        }

        internal Task RemoveAllAsync()
        {
            return Connection.ExecuteAsync("DELETE FROM profiles");
        }

        internal Task InsertAsync(Profile profile)
        {
            return Connection.InsertAsync(profile);
        }

        internal async Task<Profile> GetCurrentAsync()
        {
            return (await Connection.QueryAsync<Profile>("SELECT * FROM profiles LIMIT 1")).First();
        }
    }
}