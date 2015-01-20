using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Feed
{
    class Storage : FeedService.IStorage, FeedProvider.IStorage
    {
        public Task ClearOldLinkedTagsAsync(ID id)
        {
            throw new NotImplementedException();
        }

        public Task CreateTagIfNotExistsAsync(ID id)
        {
            throw new NotImplementedException();
        }

        public Task<PostCollectionState> GetPostsAsync(ID id)
        {
            throw new NotImplementedException();
        }

        public Task SaveLinkedTagAsync(ID id, TagLinkedTag linkedTag)
        {
            throw new NotImplementedException();
        }

        public Task SavePostToDatabaseAsync(ID id, Post post)
        {
            throw new NotImplementedException();
        }

        public Task UpdateNextPageForTagAsync(ID id, int nextPage)
        {
            throw new NotImplementedException();
        }
    }
}
