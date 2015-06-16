using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    class PostRepository : Repository<Post>
    {
        public async Task InsertOrUpdateAsync(Post row)
        {
            var old = await GetAsync(row.PostId);
            if (old == null)
                InsertAsync(row);
            else
            {
                row.Id = old.Id;
                UpdateAsync(row);
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