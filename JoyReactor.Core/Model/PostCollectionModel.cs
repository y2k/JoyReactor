using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class PostCollectionModel
    {
        SQLiteConnection connection = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        #region New Methods

        public Task<PostCollectionState> Get(ID id)
        {
            return Task.Run(() =>
                {
                    return new PostCollectionState
                    {
                        Posts = GetPostsForTag(id.SerializeToString()),
                        NewItemsCount = GetNewItemsCount(id.SerializeToString()),
                        DividerPosition = GetDividerPosition(id.SerializeToString()),
                    };
                });
        }

        List<Post> GetPostsForTag(string tagId)
        {
            return connection.SafeQuery<Post>(
                "SELECT p.* " +
                "FROM tag_post t " +
                "JOIN posts p ON p.Id = t.PostId " +
                "WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?) " +
                "AND (Status = ? OR Status = ?)",
                tagId, TagPost.StatusOld, TagPost.StatusActual);
        }

        int GetNewItemsCount(string tagId)
        {
            return connection.SafeExecuteScalar<int>(
                "SELECT COUNT(*) " +
                "FROM tag_post " +
                "WHERE TagId IN ( " +
                "   SELECT Id " +
                "   FROM tags " +
                "   WHERE TagId = ? AND Status = ?)",
                tagId, TagPost.StatusPending);
        }

        int GetDividerPosition(string tagId)
        {
            //if (GetNewItemsCount(tagId) > 0)
            //    return 0;
            return connection.SafeExecuteScalar<int>(
                "SELECT COUNT(*) " +
                "FROM tag_post " +
                "WHERE TagId IN ( " +
                "   SELECT Id " +
                "   FROM tags " +
                "   WHERE TagId = ?) " +
                "AND Status = ?",
                tagId, TagPost.StatusActual);
        }

        public Task SyncFirstPage(ID id)
        {
            return Task.Run(() =>
                {
                    CreateTagIfNotExists(id);
                    ClearOldLinkedTags(id);

                    var parser = GetParserForTag(id);
//                    parser.Cookies = GetSiteCookies(id);
                    parser.NewTagInformation += (sender, information) =>
                        UpdateTagInformation(id.SerializeToString(), information);

                    var organizer = new FirstPagePostSorter(connection, id.SerializeToString());
                    parser.NewPost += (sender, post) =>
                    {
                        var newid = SavePostToDatabase(id, post);
                        organizer.AddNewPost(newid);
                    };

                    parser.NewLinkedTag += (sender, e) => SaveLinkedTag(id, e);

                    parser.ExtractTag(id.Tag, id.Type, null);
                    organizer.SaveChanges();

                    TagCollectionModel.OnInvalidateEvent();
                });
        }

        void ClearOldLinkedTags(ID id)
        {
            connection.SafeExecute(
                "DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                id.SerializeToString());
        }

        void SaveLinkedTag(ID id, ExportLinkedTag linkedTag)
        {
            int tagId = connection.SafeQuery<Tag>(
                            "SELECT * FROM tags WHERE TagId = ?",
                            id.SerializeToString()).First().Id;
            var dbTag = new TagLinkedTag
            {
                ParentTagId = tagId,
                GroupName = linkedTag.group,
                Image = linkedTag.image,
                TagId = linkedTag.value,
                Title = linkedTag.name,
            };
            connection.SafeInsert(dbTag);
        }

        SiteParser GetParserForTag(ID id)
        {
            var parsers = ServiceLocator.Current.GetInstance<SiteParser[]>();
            return parsers.First(s => s.ParserId == id.Site);
        }

        void UpdateTagInformation(string tagId, ExportTagInformation information)
        {
            var t = connection.SafeQuery<Tag>("SELECT * FROM tags WHERE TagId = ?", tagId).FirstOrDefault()
                    ?? new Tag { BestImage = information.Image, TagId = tagId };
            t.NextPage = information.NextPage;
            if (t.Id == 0)
                connection.SafeInsert(t);
            else
                connection.SafeUpdate(t);
        }

        void ClearDatabaseFromPosts(ID id)
        {
            connection.SafeExecute(
                "UPDATE tags SET Timestamp = ? WHERE TagId = ?", 
                TimestampNow(), id.SerializeToString());
            // Удаление постов тега
            connection.SafeExecute(
                "DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)", 
                id.SerializeToString());
            // Удаление связанных тегов
            connection.SafeExecute(
                "DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)", 
                id.SerializeToString());
        }

        void CreateTagIfNotExists(ID id)
        {
            if (!IsTagExists(id.SerializeToString()))
                connection.SafeInsert(new Tag
                    {
                        TagId = id.SerializeToString(),
                        Flags = Tag.FlagSystem
                    });
        }

        bool IsTagExists(string flatTagId)
        {
            return connection.SafeExecuteScalar<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", flatTagId) > 0;
        }

        public Task SyncNextPage(ID id)
        {
            return Task.Run(() =>
                {
                    var parser = GetParserForTag(id);
//                    parser.Cookies = GetSiteCookies(id);
                    var organizer = new NextPagePostSorter(connection, id.SerializeToString());
                    parser.NewPost += (sender, post) =>
                    {
                        var newid = SavePostToDatabase(id, post);
                        organizer.AddNewPost(newid);
                    };
                    parser.NewTagInformation += (sender, information) => UpdateTagInformation(id.SerializeToString(), information);
                    parser.ExtractTag(id.Tag, id.Type, GetNextPageForTag(id));
                    organizer.SaveChanges();
                });
        }

        int SavePostToDatabase(ID listId, ExportPost post)
        {
            var p = Convert(listId.Site, post);
            p.Id = connection.SafeExecuteScalar<int>("SELECT Id FROM posts WHERE PostId = ?", p.PostId);
            if (p.Id == 0)
                connection.SafeInsert(p);
            else
                connection.SafeUpdate(p);
            return p.Id;
        }

        int GetNextPageForTag(ID id)
        {
            return connection
                .SafeQuery<Tag>("SELECT * FROM tags WHERE TagId = ?", id.SerializeToString())
                .First().NextPage;
        }

        public Task ApplyNewItems(ID id)
        {
            return Task.Run(() =>
                {
                    connection.SafeRunInTransaction(() =>
                        {
                            var tagId = connection.SafeQuery<TagPost>(
                                            "SELECT Id FROM tags WHERE TagId = ?", 
                                            id.SerializeToString()).First().Id;
                            var links = connection.SafeQuery<TagPost>(
                                            "SELECT * FROM tag_post WHERE TagId = ?", 
                                            tagId);
                            links.Sort(new TagPostComparer());
                            connection.SafeExecute("DELETE FROM tag_post WHERE TagId = ?", tagId);
                            foreach (var s in links)
                            {
                                s.Id = 0;
                                if (s.Status == TagPost.StatusPending)
                                    s.Status = TagPost.StatusActual;
                            }
                            connection.SafeInsertAll(links);
                        });
                });
        }

        public Task Reset(ID id)
        {
            return Task.Run(async () =>
                {
                    connection.SafeExecute(
                        "DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                        id.SerializeToString());
                    await SyncFirstPage(id);
                });
        }

        class TagPostComparer : IComparer<TagPost>
        {
            #region IComparer implementation

            public int Compare(TagPost x, TagPost y)
            {
                return x.Status == y.Status ? x.Id - y.Id : y.Status - x.Status;
            }

            #endregion
        }

        #endregion

        #region IPostCollectionModel implementation

        public Task<List<Post>> GetPostsAsync(ID id)
        {
            return Task.Run(() =>
                {
                    var sql = "" +
                              "SELECT * " +
                              "FROM posts " +
                              "WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?))";
                    return connection.SafeQuery<Post>(sql, id.SerializeToString());
                });
        }

        public int GetCount(ID id)
        {
            return connection.SafeExecuteScalar<int>(
                "SELECT COUNT(*) FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                id.SerializeToString());
        }

        public Task<int> GetCountAsync(ID id)
        {
            return Task.Run(() => GetCount(id));
        }

        #endregion

        #region Private methods

//        IDictionary<string, string> GetSiteCookies(ID id)
//        {
//            var p = connection.SafeDeferredQuery<Profile>("SELECT * FROM profiles WHERE Site = ?", "" + id.Site).FirstOrDefault();
//            return p == null ? new Dictionary<string, string>() : DeserializeObject<Dictionary<string, string>>(p.Cookie);
//        }

        static long TimestampNow()
        {
            return DateTime.Now.ToFileTimeUtc() / 10000L;
        }

        static IDictionary<string, string> DeserializeObject<T>(string o)
        {
            return o.Split(';').Select(s => s.Split('=')).ToDictionary(s => s[0], s => s[1]);
        }

        Post Convert(ID.SiteParser parserId, ExportPost p)
        {
            return new Post
            {
                PostId = parserId + "-" + p.Id,
                CommentCount = p.CommentCount,
                Coub = p.Coub,
                Created = p.Created,
                Image = p.Image,
                ImageHeight = p.ImageHeight,
                ImageWidth = p.ImageWidth,
                Rating = p.Rating,
                Title = p.Title,
                UserImage = p.UserImage,
                UserName = p.UserName,
            };
        }

        #endregion
    }

    public class PostCollectionState
    {
        public List<Post> Posts { get; set; }

        public int NewItemsCount { get; set; }

        public int DividerPosition { get; set; }
    }

    public enum SyncFlags
    {
        None,
        Next,
        First
    }
}