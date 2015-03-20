using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
    public class MemoryStorage: TagCollectionModel.Storage
    {
        public static MemoryStorage Intance { get; } = new MemoryStorage();

        Dictionary<ID, ICollection<TagGroup>> linkedTags = new Dictionary<ID, ICollection<TagGroup>>();

        MemoryStorage()
        {
        }

        public Task SaveLinkedTagsAsync(ID id, string groupName, ICollection<Tag> tags)
        {
            if (!linkedTags.ContainsKey(id))
                linkedTags[id] = new List<TagGroup>();
            linkedTags[id].Add(new TagGroup { Title = groupName, Tags = tags });
            return GetCompletedTask();
        }

        public Task RemoveLinkedTagAsync(ID id)
        {
            linkedTags.Remove(id);
            return GetCompletedTask();
        }

        public Task<ICollection<TagGroup>> GetLinkedTagsAsync(ID id)
        {
            var tags = linkedTags
                .Where(s => s.Key == id)
                .Select(s => s.Value)
                .FirstOrDefault() ?? new List<TagGroup>();
            return Task.FromResult(tags);
        }

        static Task GetCompletedTask()
        {
            return Task.FromResult(false);
        }
    }
}