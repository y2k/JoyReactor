using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    class PostRepository : Repository<Post>
    {
        public Task InsertOrUpdateAsync(Post row)
        {
            return InsertOrUpdateInner(row);
        }

        public async Task UpdateOrInsertAllAsync(ICollection<Post> items)
        {
            foreach (var s in items)
                await InsertOrUpdateInner(s);
        }

        async Task InsertOrUpdateInner(Post row)
        {
            var old = await GetAsync(row.PostId);
            if (old == null)
                await InsertAsync(row);
            else
            {
                row.Id = old.Id;
                await UpdateAsync(row);
            }
        }

        public Task<List<Post>> GetAllAsync(int tagId)
        {
            return Connection.QueryAsync<Post>(@"
                SELECT p.* 
                FROM tag_post t 
                JOIN posts p ON p.Id = t.PostId 
                WHERE TagId = ?
                ", tagId);
        }

        public async Task<Post> GetAsync(string postId)
        {
            var rows = await Connection.QueryAsync<Post>(@"
            SELECT * 
            FROM posts 
            WHERE PostId = ?", postId);
            return rows.FirstOrDefault();
        }
    }
}