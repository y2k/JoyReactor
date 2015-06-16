using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class CommonRepository : Repository<object>, FeedService.IFeedRepository, IProviderStorage, PostService.IStorage
    {
        public Task ClearOldLinkedTagsAsync(ID id)
        {
            return Connection.ExecuteAsync(
                "DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                id.SerializeToString());
        }

        public async Task CreateTagIfNotExistsAsync(ID id)
        {
            if (!(await IsTagExists(id)))
                await Connection.InsertAsync(new Tag
                {
                    TagId = id.SerializeToString(),
                    Flags = Tag.FlagSystem
                });
        }

        async Task<bool> IsTagExists(ID id)
        {
            return (await Connection.ExecuteScalarAsync<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id.SerializeToString())) > 0;
        }

        #region GetPostsAsync

        Task<int> GetDividerPosition(ID id)
        {
            return Connection.ExecuteScalarAsync<int>(
                "SELECT COUNT(*) " +
                "FROM tag_post " +
                "WHERE TagId IN ( " +
                "   SELECT Id " +
                "   FROM tags " +
                "   WHERE TagId = ?) " +
                "AND Status = ?",
                id.SerializeToString(), TagPost.StatusActual);
        }

        #endregion

        public async Task SaveLinkedTagAsync(ID id, TagLinkedTag linkedTag)
        {
            int tagId = (await Connection.QueryAsync<Tag>(
                            "SELECT * FROM tags WHERE TagId = ?",
                            id.SerializeToString())).First().Id;
            linkedTag.ParentTagId = tagId;
            await Connection.InsertAsync(linkedTag);
        }

        public async Task SavePostToDatabaseAsync(ID id, Post post)
        {
            post.Id = await Connection.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", post.PostId);
            if (post.Id == 0)
                await Connection.InsertAsync(post);
            else
                await Connection.UpdateAsync(post);
        }

        public async Task UpdateNextPageForTagAsync(ID id, int nextPage)
        {
            var t = (await Connection.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())).First();
            t.NextPage = nextPage;
            if (t.Id == 0)
                await Connection.InsertAsync(t);
            else
                await Connection.UpdateAsync(t);
        }

        public async Task ApplyNewItemsAsync(ID id)
        {
            var tagId = (await Connection.QueryAsync<TagPost>(
                            "SELECT Id FROM tags WHERE TagId = ?",
                            id.SerializeToString())).First().Id;
            var links = await Connection.QueryAsync<TagPost>(
                            "SELECT * FROM tag_post WHERE TagId = ?",
                            tagId);
            links.Sort((x, y) => x.Status == y.Status ? x.Id - y.Id : y.Status - x.Status);
            await Connection.ExecuteAsync("DELETE FROM tag_post WHERE TagId = ?", tagId);
            foreach (var s in links)
            {
                s.Id = 0;
                s.Status = s.IsPending ? TagPost.StatusActual : TagPost.StatusOld;
            }
            await Connection.InsertAllAsync(links);
        }

        public async Task<int> GetNextPageForTagAsync(ID id)
        {
            return (await Connection.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())).First().NextPage;
        }

        public Task ClearTagFromPostsAsync(ID id)
        {
            return Connection.ExecuteAsync("DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)", id.SerializeToString());
        }

        async Task IProviderStorage.SaveNewOrUpdatePostAsync(Post post)
        {
            post.Id = await Connection.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", post.PostId);
            if (post.Id == 0)
                await Connection.InsertAsync(post);
            else
                await Connection.UpdateAsync(post);
        }

        async Task IProviderStorage.UpdateTagInformationAsync(ID id, string image, int nextPage, bool hasNextPage)
        {
            var t = (await Connection.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())).FirstOrDefault()
                    ?? new Tag { BestImage = image, TagId = id.SerializeToString() };
            t.NextPage = nextPage;
            if (t.Id == 0)
                await Connection.InsertAsync(t);
            else
                await Connection.UpdateAsync(t);
        }

        async Task IProviderStorage.ReplacePostAttachments(string postId, List<Attachment> attachments)
        {
            var parentId = await Connection.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", postId);
            await Connection.ExecuteAsync("DELETE FROM attachments WHERE ParentId = ? AND ParentType = ?", parentId, Attachment.ParentPost);
            foreach (var a in attachments)
            {
                a.ParentId = parentId;
                a.ParentType = Attachment.ParentPost;
                await Connection.InsertAsync(a);
            }
        }

        Task IProviderStorage.RemovePostComments(string postId)
        {
            return Connection.ExecuteAsync("DELETE FROM comments WHERE PostId IN (SELECT Id FROM posts WHERE PostId = ?)", postId);
        }

        async Task IProviderStorage.SaveNewPostCommentAsync(string postId, int parrentCommentId, Comment comment, string[] attachments)
        {
            comment.ParentCommentId = parrentCommentId;
            comment.PostId = await Connection.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", postId);
            await Connection.InsertAsync(comment);

            foreach (var a in attachments)
                await Connection.InsertAsync(
                    new Attachment
                    {
                        ParentType = Attachment.ParentComment,
                        ParentId = comment.Id,
                        Type = Attachment.TypeImage,
                        Url = a,
                    });
        }

        async Task IProviderStorage.SaveNewOrUpdateProfileAsync(Profile profile)
        {
            // TODO: придумать способ получше, что бы сохранять куки при обновление профиля
            profile.Cookie = await Connection.ExecuteScalarAsync<string>("SELECT Cookie FROM profiles");
            await Connection.ExecuteAsync("DELETE FROM profiles");
            await Connection.InsertAsync(profile);
        }

        async Task IProviderStorage.ReplaceCurrentUserReadingTagsAsync(IEnumerable<string> readingTags)
        {
            foreach (var t in readingTags)
            {
                var id = ID.Factory.NewTag(t).SerializeToString();
                int c = await Connection.ExecuteScalarAsync<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id);
                if (c == 0)
                    await Connection.InsertAsync(
                        new Tag
                        {
                            Flags = Tag.FlagWebRead | Tag.FlagShowInMain,
                            TagId = id,
                            Title = t,
                        });
            }
        }

        Task<List<Comment>> PostService.IStorage.GetChildCommentsAsync(int postId, int commentId)
        {
            return Connection.QueryAsync<Comment>(@"
SELECT
c.*,
(SELECT COUNT(*) FROM comments WHERE ParentCommentId = c.Id) AS ChildCount,
a.Url AS _Attachments
FROM comments c
LEFT JOIN attachments a ON a.ParentType == 1 AND a.ParentId = c.Id
WHERE c.PostId = ? AND c.ParentCommentId = ?
GROUP BY c.Id
ORDER BY c.Rating DESC, ChildCount DESC
                ", postId, commentId);
        }

        async Task<Comment> PostService.IStorage.GetCommentAsync(int commentId)
        {
            return (await Connection.QueryAsync<Comment>(
                "SELECT " +
                "c.*, " +
                "(SELECT COUNT(*) FROM comments WHERE ParentCommentId = c.Id) AS ChildCount " +
                "FROM comments c " +
                "WHERE c.Id = ? ", commentId)).First();
        }

        Task PostService.IStorage.CreateMainTagAsync(string name)
        {
            return Connection.InsertAsync(new Tag
            {
                TagId = ID.Factory.NewTag(name.ToLower()).SerializeToString(),
                Title = name,
                Flags = Tag.FlagShowInMain
            });
        }

        Task<List<RelatedPost>> PostService.IStorage.GetRelatedPostsAsync(int postId)
        {
            return Connection.QueryAsync<RelatedPost>("SELECT * FROM related_posts WHERE ParentPostId = ?", postId);
        }

        async Task<Post> PostService.IStorage.GetPostAsync(int postId)
        {
            return (await Connection.QueryAsync<Post>("SELECT * FROM posts WHERE id = ?", postId)).First();
        }

        Task<List<Attachment>> PostService.IStorage.GetAttachmentsAsync(int postId)
        {
            return Connection.QueryAsync<Attachment>(
                "SELECT * FROM attachments WHERE ParentType = ? AND ParentId = ?",
                Attachment.ParentPost, postId);
        }

        async Task IProviderStorage.SaveRelatedPostsAsync(string postId, List<RelatedPost> posts)
        {
            var id = await Connection.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", postId);
            await Connection.ExecuteAsync("DELETE FROM related_posts WHERE ParentPostId = ?", id);
            foreach (var p in posts)
            {
                p.ParentPostId = id;
                await Connection.InsertAsync(p);
            }
        }

        public Task SaveLinkedTagsAsync(ID id, string groupName, ICollection<Tag> tags)
        {
            return MemoryStorage.Intance.SaveLinkedTagsAsync(id, groupName, tags);
        }

        public Task RemoveLinkedTagAsync(ID id)
        {
            return MemoryStorage.Intance.RemoveLinkedTagAsync(id);
        }
    }
}