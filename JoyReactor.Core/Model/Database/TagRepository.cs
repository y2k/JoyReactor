using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    class TagRepository : Repository
    {
        public Task<Tag> GetAsync(string tagId)
        {
            return Connection.QueryFirstAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", tagId);
        }
    }
}