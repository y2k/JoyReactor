using JoyReactor.Core.Model.DTO;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class TagRepository : Repository
    {
        public async Task<Tag> GetAsync(string tagId)
        {
            return (await Connection.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", tagId)).First();
        }

        internal Task<int> CountAsync(string id)
        {
            return Connection.ExecuteScalarAsync<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id);
        }

        internal Task InsertAsync(Tag tag)
        {
            return Connection.InsertAsync(tag);
        }
    }
}