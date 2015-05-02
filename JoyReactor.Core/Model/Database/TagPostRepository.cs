using System.Collections.Generic;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    class TagPostRepository : Repository
    {
        public Task<List<TagPost>> GetAllAsync(int tagId)
        {
            return Connection.QueryAsync<TagPost>("SELECT * FROM TagPost WHERE TagId = ?", tagId);
        }

        public Task RemoveAsync(int tagId)
        {
            return Connection.ExecuteAsync("DELETE FROM TagPost WHERE TagId = ?", tagId);
        }

        public Task<int> AddAsync(TagPost item)
        {
            return Connection.InsertAsync(item);
        }
    }
}