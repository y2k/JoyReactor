using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Model
{
    static class ListStorageFactory
    {
        public static IProviderListStorage NewInstance(ID id, bool isFirstPage)
        {
            return isFirstPage
                ? (IProviderListStorage)new FirstPagePostSorter(id.SerializeToString())
                : new NextPagePostSorter(id.SerializeToString());
        }

        class FirstPagePostSorter : IProviderListStorage
        {
            List<int> uniquePostIds = new List<int>();
            List<int> dublicatePostIds = new List<int>();

            string tagId;
            List<TagPost> currentTagPosts;
            Tag tag;

            public FirstPagePostSorter(string tagId)
            {
                this.tagId = tagId;
            }

            public async Task AddPost(Post post)
            {
                if (currentTagPosts == null)
                    await Initialize();

                if (currentTagPosts.Any(s => s.PostId == post.Id))
                    dublicatePostIds.Add(post.Id);
                else
                    uniquePostIds.Add(post.Id);
            }

            async Task Initialize()
            {
                tag = await new TagRepository().GetAsync(tagId);
                currentTagPosts = (await new TagPostRepository().GetAllAsync(tag.Id)).Where(s => s.Status != 0).ToList();
            }

            public async Task CommitAsync()
            {
                await new TagPostRepository().RemoveAllAsync(tag.Id);

                if (IsTagEmpty)
                {
                    foreach (var id in uniquePostIds)
                        await InsertTagPost(id, TagPost.StatusActual, false);
                }
                else if (IsTagChanged)
                {
                    foreach (var id in uniquePostIds)
                        await InsertTagPost(id, 0, true);
                    foreach (var tagPost in currentTagPosts)
                        await InsertTagPost(tagPost.PostId, tagPost.Status, dublicatePostIds.Contains(tagPost.PostId));
                }
                else
                {
                    foreach (var tagPost in currentTagPosts)
                        await InsertTagPost(tagPost.PostId, dublicatePostIds.Contains(tagPost.PostId) ? TagPost.StatusActual : TagPost.StatusOld, false);
                }
            }

            bool IsTagEmpty
            {
                get { return currentTagPosts.Count == 0; }
            }

            bool IsTagChanged
            {
                get
                {
                    return uniquePostIds.Count != 0
                    || currentTagPosts.Take(dublicatePostIds.Count).Any(s => !dublicatePostIds.Contains(s.PostId));
                }
            }

            Task InsertTagPost(int postId, int status, bool isPenging)
            {
                var item = new TagPost { TagId = tag.Id, PostId = postId, Status = status, IsPending = isPenging };
                return new TagPostRepository().AddAsync(item);
            }
        }

        class NextPagePostSorter : IProviderListStorage
        {
            List<int> currentActualPostIds = new List<int>();
            List<int> currentOldPostIds = new List<int>();
            List<int> newPostIds = new List<int>();

            string tagId;
            Tag tag;

            public NextPagePostSorter(string tagId)
            {
                this.tagId = tagId;
            }

            public async Task AddPost(Post post)
            {
                if (tag == null)
                    await Initialize();

                if (!currentActualPostIds.Contains(post.Id))
                    newPostIds.Add(post.Id);
                if (currentOldPostIds.Contains(post.Id))
                    currentOldPostIds.Remove(post.Id);
            }

            async Task Initialize()
            {
                tag = await new TagRepository().GetAsync(tagId);
                await LoadPostIdsForTag();
            }

            async Task LoadPostIdsForTag()
            {
                var links = await new TagPostRepository().GetAllAsync(tag.Id);

                foreach (var s in links)
                {
                    switch (s.Status)
                    {
                        case TagPost.StatusActual:
                            currentActualPostIds.Add(s.PostId);
                            break;
                        case TagPost.StatusOld:
                            currentOldPostIds.Add(s.PostId);
                            break;
                        default:
                            throw new InvalidOperationException();
                    }
                }
            }

            [Obsolete]
            public void SaveChanges()
            {
                CommitAsync().Wait();
            }

            public async Task CommitAsync()
            {
                await new TagPostRepository().RemoveAllAsync(tag.Id);
                foreach (var id in currentActualPostIds)
                    await InsertTagPost(id, TagPost.StatusActual);
                foreach (var id in newPostIds)
                    await InsertTagPost(id, TagPost.StatusActual);
                foreach (var id in currentOldPostIds)
                    await InsertTagPost(id, TagPost.StatusOld);
            }


            Task InsertTagPost(int id, int status)
            {
                return new TagPostRepository().AddAsync(new TagPost { TagId = tag.Id, PostId = id, Status = status });
            }
        }
    }
}