using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class ProfileRepository : Repository
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
    }
}