using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Feed;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System;

namespace JoyReactor.Core.Model.Database
{
    class SQLiteStorage : FeedService.IStorage, JoyReactorProvider.IStorage, PostService.IStorage, CommentService.IStorage
    {
        SQLiteConnection db = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task ClearOldLinkedTagsAsync(ID id)
        {
            return db.ExecuteAsync(
                "DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                id.SerializeToString());
        }

        public async Task CreateTagIfNotExistsAsync(ID id)
        {
            if (!(await IsTagExists(id)))
                await db.InsertAsync(new Tag
                {
                    TagId = id.SerializeToString(),
                    Flags = Tag.FlagSystem
                });
        }

        async Task<bool> IsTagExists(ID id)
        {
            return (await db.ExecuteScalarAsync<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id.SerializeToString())) > 0;
        }

        #region GetPostsAsync

        public async Task<PostCollectionState> GetPostsAsync(ID id)
        {
            return new PostCollectionState
            {
                Posts = await GetPostsForTag(id),
                NewItemsCount = await GetNewItemsCount(id),
                DividerPosition = await GetDividerPosition(id),
            };
        }

        Task<List<Post>> GetPostsForTag(ID id)
        {
            return db.QueryAsync<Post>(
                "SELECT p.* " +
                "FROM tag_post t " +
                "JOIN posts p ON p.Id = t.PostId " +
                "WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?) " +
                "AND (Status = ? OR Status = ?)",
                id.SerializeToString(), TagPost.StatusOld, TagPost.StatusActual);
        }

        Task<int> GetNewItemsCount(ID id)
        {
            return db.ExecuteScalarAsync<int>(
                "SELECT COUNT(*) " +
                "FROM tag_post " +
                "WHERE TagId IN ( " +
                "   SELECT Id " +
                "   FROM tags " +
                "   WHERE TagId = ? AND Status = ?)",
                id.SerializeToString(), TagPost.StatusPending);
        }

        Task<int> GetDividerPosition(ID id)
        {
            return db.ExecuteScalarAsync<int>(
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
            int tagId = (await db.QueryAsync<Tag>(
                "SELECT * FROM tags WHERE TagId = ?",
                id.SerializeToString())).First().Id;
            linkedTag.ParentTagId = tagId;
            await db.InsertAsync(linkedTag);
        }

        public async Task SavePostToDatabaseAsync(ID id, Post post)
        {
            post.Id = await db.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", post.PostId);
            if (post.Id == 0)
                await db.InsertAsync(post);
            else
                await db.UpdateAsync(post);
        }

        public async Task UpdateNextPageForTagAsync(ID id, int nextPage)
        {
            var t = (await db.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())).First();
            t.NextPage = nextPage;
            if (t.Id == 0)
                await db.InsertAsync(t);
            else
                await db.UpdateAsync(t);
        }

        public Task ApplyNewItemsAsync(ID id)
        {
            return db.RunInTransactionAsync(() =>
            {
                var tagId = db.SafeQuery<TagPost>(
                                "SELECT Id FROM tags WHERE TagId = ?",
                                id.SerializeToString()).First().Id;
                var links = db.SafeQuery<TagPost>(
                                "SELECT * FROM tag_post WHERE TagId = ?",
                                tagId);
                links.Sort((x, y) => x.Status == y.Status ? x.Id - y.Id : y.Status - x.Status);
                db.SafeExecute("DELETE FROM tag_post WHERE TagId = ?", tagId);
                foreach (var s in links)
                {
                    s.Id = 0;
                    if (s.Status == TagPost.StatusPending)
                        s.Status = TagPost.StatusActual;
                }
                db.SafeInsertAll(links);
            });
        }

        public async Task<int> GetNextPageForTagAsync(ID id)
        {
            return (await db.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())).First().NextPage;
        }

        public Task ClearTagFromPostsAsync(ID id)
        {
            return db.ExecuteAsync("DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)", id.SerializeToString());
        }

        async Task JoyReactorProvider.IStorage.SaveNewOrUpdatePostAsync(Post post)
        {
            post.Id = await db.ExecuteScalarAsync<int>("SELECT Id FROM posts WHERE PostId = ?", post.PostId);
            if (post.Id == 0) await db.InsertAsync(post);
            else await db.UpdateAsync(post);
        }

        async Task JoyReactorProvider.IStorage.UpdateTagInformationAsync(ID id, string image, int nextPage, bool hasNextPage)
        {
            var t = (await db.QueryAsync<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())).FirstOrDefault()
                    ?? new Tag { BestImage = image, TagId = id.SerializeToString() };
            t.NextPage = nextPage;
            if (t.Id == 0) await db.InsertAsync(t);
            else await db.UpdateAsync(t);
        }

        Task JoyReactorProvider.IStorage.ReplacePostAttachments(string postId, List<Attachment> attachments)
        {
            return db.RunInTransactionAsync(() =>
            {
                var parentId = db.ExecuteScalar<int>("SELECT Id FROM posts WHERE PostId = ?", postId);
                db.Execute("DELETE FROM attachments WHERE ParentId = ? AND ParentType = ?", parentId, Attachment.ParentPost);
                foreach (var a in attachments)
                {
                    a.ParentId = parentId;
                    a.ParentType = Attachment.ParentPost;
                    db.Insert(a);
                }
            });
        }

        Task JoyReactorProvider.IStorage.RemovePostComments(string postId)
        {
            return db.ExecuteAsync("DELETE FROM comments WHERE PostId IN (SELECT Id FROM posts WHERE PostId = ?)");
        }

        Task JoyReactorProvider.IStorage.SaveNewPostCommentAsync(string postId, string parrentCommentId, Comment comment, string[] attachments)
        {
            return db.RunInTransactionAsync(() =>
            {
                db.Insert(comment);
                db.Insert(new CommentLink
                {
                    CommentId = comment.Id,
                    ParentCommentId = db.ExecuteScalar<int>("SELECT Id FROM comments WHERE PostId = ? AND CommentId = ?", postId, parrentCommentId),
                });

                foreach (var a in attachments)
                    db.Insert(new Attachment
                    {
                        ParentType = Attachment.ParentComment,
                        ParentId = comment.Id,
                        Type = Attachment.TypeImage,
                        Url = a,
                    });
            });
        }

        async Task JoyReactorProvider.IStorage.SaveNewOrUpdateProfileAsync(Profile profile)
        {
            await db.ExecuteAsync("DELETE FROM profiles");
            await db.InsertAsync(profile);
        }

        Task JoyReactorProvider.IStorage.ReplaceCurrentUserReadingTagsAsync(IEnumerable<string> readingTags)
        {
            return db.RunInTransactionAsync(() =>
            {
                foreach (var t in readingTags)
                {
                    var id = ID.Factory.NewTag(t).SerializeToString();
                    int c = db.ExecuteScalar<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id);
                    if (c == 0)
                    {
                        db.Insert(new Tag
                        {
                            Flags = Tag.FlagWebRead | Tag.FlagShowInMain,
                            TagId = id,
                            Title = t,
                        });
                    }
                }
            });
        }

        async Task<Post> PostService.IStorage.GetPostWithAttachmentsAsync(int postId)
        {
            var post = await db.QueryFirstAsync<Post>("SELECT * FROM posts WHERE id = ?", postId);
            post.Attachments = await db.QueryAsync<Attachment>(
                "SELECT * FROM attachments WHERE ParentType = ? AND ParentId = ?",
                Attachment.ParentPost, postId);
            return post;
        }

        Task<List<Comment>> CommentService.IStorage.GetChildCommentsAsync(int postId, int commentId)
        {
            if (commentId == 0)
            {
                return db.QueryAsync<Comment>(
                    "SELECT c.*, (SELECT COUNT(*) FROM comment_links WHERE ParentCommentId = c.Id) AS ChildCount " +
                    "FROM comments c " +
                    "WHERE c.PostId = ? AND c.Id NOT IN (SELECT CommentId FROM comment_links) " +
                    "ORDER BY c.Rating DESC, ChildCount DESC ",
                    postId);
            }
            else
            {
                return db.QueryAsync<Comment>(
                    "SELECT c.*, (SELECT COUNT(*) FROM comment_links WHERE ParentCommentId = c.Id) AS ChildCount FROM comments c WHERE c.PostId = ? AND c.Id IN (" +
                    "   SELECT CommentId FROM comment_links WHERE ParentCommentId = ?) " +
                    "ORDER BY c.Rating DESC, ChildCount DESC",
                    postId, commentId);
            }
        }
    }
}