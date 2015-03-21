using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;

namespace JoyReactor.Core.Model
{
    class OrderedListStorage : JoyReactorProvider.IListStorage
    {
        IPageSorter sorter;

        internal OrderedListStorage(ID id, bool isFirstPage)
        {
            sorter = isFirstPage
                ? (IPageSorter)new FirstPagePostSorter(id.SerializeToString())
                : new NextPagePostSorter(id.SerializeToString());
        }

        public void AddPost(Post post)
        {
            sorter.AddNewPost(post.Id);
        }

        public Task CommitAsync()
        {
            return sorter.SaveChangesAsync();
        }

        interface IPageSorter
        {
            void AddNewPost(int newid);

            Task SaveChangesAsync();
        }

        class FirstPagePostSorter : IPageSorter
        {
            List<int> newUniqePostIds = new List<int>();
            List<int> newDublicatePosts = new List<int>();
            List<int> currentPostIds;
            SQLiteConnection db;
            int tagId;

            public FirstPagePostSorter(string tagId) : this(ServiceLocator.Current.GetInstance<SQLiteConnection>(), tagId) { }

            public FirstPagePostSorter(SQLiteConnection db, string tagId)
            {
                this.db = db;
                LoadTagId(tagId);
                LoadPostIdsForTag();
            }

            void LoadTagId(string textTagId)
            {
                tagId = db.SafeExecuteScalar<int>("SELECT Id FROM tags WHERE TagId = ?", textTagId);
            }

            void LoadPostIdsForTag()
            {
                currentPostIds = db
                    .SafeQuery<TagPost>(
                    "SELECT PostId FROM tag_post WHERE TagId = ? AND (Status = ? OR Status = ?)",
                    tagId, TagPost.StatusActual, TagPost.StatusOld)
                    .Select(s => s.PostId)
                    .ToList();
            }

            public void AddNewPost(int newid)
            {
                if (currentPostIds.Contains(newid))
                    newDublicatePosts.Add(newid);
                else
                    newUniqePostIds.Add(newid);
            }

            public Task SaveChangesAsync()
            {
                return db.RunInTransactionAsync(() =>
                {
                    db.SafeExecute("DELETE FROM tag_post WHERE TagId = ?", tagId);

                    if (IsFirstExecution)
                    {
                        foreach (var id in newUniqePostIds)
                            InsertTagPost(id, TagPost.StatusActual);
                    }
                    else
                    {
                        foreach (var id in newUniqePostIds)
                            InsertTagPost(id, TagPost.StatusPending);
                        foreach (var id in currentPostIds)
                            InsertTagPost(id, newDublicatePosts.Contains(id) ? TagPost.StatusActual : TagPost.StatusOld);
                    }
                });
            }

            private void InsertTagPost(int id, int status)
            {
                db.SafeInsert(new TagPost
                {
                    TagId = tagId,
                    PostId = id,
                    Status = status
                });
            }

            bool IsFirstExecution
            {
                get { return currentPostIds.Count <= 0; }
            }
        }

        class NextPagePostSorter : IPageSorter
        {
            List<int> currentActualPostIds = new List<int>();
            List<int> currentOldPostIds = new List<int>();
            List<int> newPostIds = new List<int>();
            SQLiteConnection db;
            int tagId;

            public NextPagePostSorter(string tagId) : this(ServiceLocator.Current.GetInstance<SQLiteConnection>(), tagId) { }

            public NextPagePostSorter(SQLiteConnection db, string tagId)
            {
                this.db = db;
                GetTagId(tagId);
                LoadPostIdsForTag();
            }

            void GetTagId(string textTagId)
            {
                tagId = db.SafeExecuteScalar<int>("SELECT Id FROM tags WHERE TagId = ?", textTagId);
            }

            void LoadPostIdsForTag()
            {
                var links = db.SafeQuery<TagPost>("SELECT PostId, Status FROM tag_post WHERE TagId = ?", tagId);
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

            public void AddNewPost(int newid)
            {
                if (!currentActualPostIds.Contains(newid))
                    newPostIds.Add(newid);
                if (currentOldPostIds.Contains(newid))
                    currentOldPostIds.Remove(newid);
            }

            [Obsolete]
            public void SaveChanges()
            {
                SaveChangesAsync().Wait();
            }

            public Task SaveChangesAsync()
            {
                return db.RunInTransactionAsync(() =>
                {
                    db.SafeExecute("DELETE FROM tag_post WHERE TagId = ?", tagId);
                    foreach (var id in currentActualPostIds)
                        InsertTagPost(id, TagPost.StatusActual);
                    foreach (var id in newPostIds)
                        InsertTagPost(id, TagPost.StatusActual);
                    foreach (var id in currentOldPostIds)
                        InsertTagPost(id, TagPost.StatusOld);
                });
            }

            void InsertTagPost(int id, int status)
            {
                db.SafeInsert(new TagPost { TagId = tagId, PostId = id, Status = status });
            }
        }
    }
}