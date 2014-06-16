using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Inject;
using Autofac;

namespace JoyReactor.Core.Model
{
    class PostCollectionModel : IPostCollectionModel
    {
        private ISiteParser[] parsers = InjectService.Instance.Get<ISiteParser[]>();

        #region IPostCollectionModel implementation

        public Task<List<Post>> GetPostsAsync(ID id, SyncFlags flags = SyncFlags.None)
        {
            return Task.Run(
                () =>
                {
                    if (flags == SyncFlags.First) SyncFirstPage(id);
                    else if (flags == SyncFlags.Next) SyncNextPage(id);

                    var sid = ToFlatId(id);
                    return MainDb.Instance.Query<Post>("SELECT * FROM posts WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?))", sid);
                });
        }

        public int GetCount(ID id)
        {
            return MainDb.Instance.ExecuteScalar<int>(
                "SELECT COUNT(*) FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                ToFlatId(id));
        }

        public Task<int> GetCountAsync(ID id)
        {
            return Task.Run(() => GetCount(id));
        }

        #endregion

        #region Private methods

        private string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }

        private void SyncFirstPage(ID id)
        {
            var ts = MainDb.Instance.ExecuteScalar<long>("SELECT Timestamp FROM tags WHERE TagId = ?", ToFlatId(id));
            if (Math.Abs(ts - TimestampNow()) < Constants.TagLifeTime) return;

            var p = parsers.First(s => s.ParserId == id.Site);

            p.ExtractTagPostCollection(id.Type, id.Tag, 0, GetSiteCookies(id), state =>
            {
                if (state.State == CollectionExportState.ExportState.Begin)
                {
                    MainDb.Instance.Execute("UPDATE tags SET Timestamp = ? WHERE TagId = ?", TimestampNow(), ToFlatId(id));

                    // Удаление постов тега
                    MainDb.Instance.Execute(
                        "DELETE FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                        ToFlatId(id));
                    // Удаление связанных тегов
                    MainDb.Instance.Execute(
                        "DELETE FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                        ToFlatId(id));
                }
                else if (state.State == CollectionExportState.ExportState.PostItem)
                {
                    SavePostToDatabase(id, state.Post);
                }
                else if (state.State == CollectionExportState.ExportState.LikendTag)
                {
                    // TODO Добавить проверку, что это первая страница
                    int tid = MainDb.Instance.ExecuteScalar<int>("SELECT Id FROM tags WHERE TagId = ?", ToFlatId(id));
                    MainDb.Instance.Insert(new TagLinkedTag
                    {
                        ParentTagId = tid,
                        GroupName = state.LinkedTag.group,
                        Image = state.LinkedTag.image,
                        Title = state.LinkedTag.name,
                        TagId = ToFlatId(new ID { Site = p.ParserId, Type = ID.TagType.Good, Tag = state.LinkedTag.value }),
                    });
                }
            });
        }

        private IDictionary<string, string> GetSiteCookies(ID id)
        {
            var p = MainDb.Instance.DeferredQuery<Profile>("SELECT * FROM profiles WHERE Site = ?", "" + id.Site).FirstOrDefault();
            return p == null ? new Dictionary<string, string>() : DeserializeObject<Dictionary<string, string>>(p.Cookie);
        }

        private void SavePostToDatabase(ID listId, ExportPost post)
        {
            var p = Convert(listId.Site, post);
            var f = ToFlatId(listId);

            p.Id = MainDb.Instance.ExecuteScalar<int>("SELECT Id FROM posts WHERE PostId = ?", p.PostId);
            if (p.Id == 0) MainDb.Instance.Insert(p);
            else MainDb.Instance.Update(p);

            var tp = new TagPost();
            tp.PostId = p.Id;
            tp.TagId = MainDb.Instance.ExecuteScalar<int>("SELECT Id FROM tags WHERE TagId = ?", f);
            tp.Status = TagPost.StatusNew;
            MainDb.Instance.Insert(tp);
        }

        private void SyncNextPage(ID id)
        {
            throw new NotImplementedException();
        }

        private static long TimestampNow()
        {
            return DateTime.Now.ToFileTimeUtc() / 10000L;
        }

        private static IDictionary<string, string> DeserializeObject<T>(string o)
        {
            return o.Split(';').Select(s => s.Split('=')).ToDictionary(s => s[0], s => s[1]);
        }

        private Post Convert(ID.SiteParser parserId, ExportPost p)
        {
            return new Post
            {
                PostId = parserId + "-" + p.id,
                CommentCount = p.commentCount,
                Coub = p.coub,
                Created = p.created,
                Image = p.image,
                ImageHeight = p.imageHeight,
                ImageWidth = p.imageWidth,
                Rating = p.rating,
                Title = p.title,
                UserImage = p.userImage,
                UserName = p.userName,
            };
        }

        #endregion
    }
}