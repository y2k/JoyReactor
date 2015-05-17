using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model
{
    class TagOrderSaver
    {
        ID id;
        bool isFirstPage;
        List<int> newPostIds;

        int tagId;

        public TagOrderSaver(ID id, bool isFirstPage, List<int> newPostIds)
        {
            this.isFirstPage = isFirstPage;
            this.id = id;
            this.newPostIds = newPostIds;
        }

        public async Task ExecuteAsync()
        {
            tagId = await GetTagId();
            var resultItems = await GetOldTagPosts();
            AddNewPosts(resultItems);

            await ReplaceTagPosts(resultItems);
        }

        async Task<List<TagPost>> GetOldTagPosts()
        {
            return isFirstPage
                ? new List<TagPost>()
                : await new TagPostRepository().GetAllAsync(tagId);
        }

        async Task<int> GetTagId()
        {
            return (await new TagRepository().GetAsync(id.SerializeToString())).Id;
        }

        void AddNewPosts(List<TagPost> oldItems)
        {
            foreach (var postId in newPostIds)
                if (oldItems.All(s => s.PostId != postId))
                    oldItems.Add(newTagPost(postId));
        }

        TagPost newTagPost(int postId)
        {
            return new TagPost { TagId = tagId, PostId = postId, Status = TagPost.StatusActual };
        }

        async Task ReplaceTagPosts(List<TagPost> items)
        {
            await new TagPostRepository().RemoveAllAsync(tagId);
            await new TagPostRepository().InsertAllAsync(items);
        }
    }
}