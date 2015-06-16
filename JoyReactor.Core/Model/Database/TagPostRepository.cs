using System.Collections.Generic;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    class TagPostRepository : Repository<TagPost>
    {
        public Task<List<TagPost>> GetAllAsync(int tagId)
        {
            return Connection.QueryAsync<TagPost>("SELECT * FROM tag_post WHERE TagId = ?", tagId);
        }

        public Task RemoveAllAsync(int tagId)
        {
            return Connection.ExecuteAsync("DELETE FROM tag_post WHERE TagId = ?", tagId);
        }

        public Task<int> AddAsync(TagPost item)
        {
            return Connection.InsertAsync(item);
        }

        public async Task ReplaceAllForTagAsync(List<TagPost> items)
        {
            await RemoveAllAsync(items[0].TagId);
            await InsertAllAsync(items);
        }
    }
}