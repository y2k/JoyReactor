using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    public class PostRepository : Repository
    {
        public Task<List<Post>> GetAllAsync(int tagId)
        {
            return Connection.QueryAsync<Post>(
                "SELECT p.* " +
                "FROM tag_post t " +
                "JOIN posts p ON p.Id = t.PostId " +
                "WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?) " +
                tagId);
        }
    }
}