using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using System.Linq;

namespace JoyReactor.Core.Model.Database
{
    class PostRepository : Repository
    {
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
            return (await Connection.QueryAsync<Post>("SELECT * FROM posts WHERE PostId = ?", postId))
                .FirstOrDefault();
        }
    }
}