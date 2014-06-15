using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Parser;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class PostModel : IPostModel
    {
        private ISiteParser[] parsers = InjectService.Locator.GetInstance<ISiteParser[]>();

        public Task<List<Comment>> GetCommentsAsync(int postId, int parentCommentId)
        {
            return Task.Run(() =>
            {
                //var comments = MainDb.Instance
                //    //.Query<Comment>("SELECT * FROM comments WHERE PostId = ? AND ParentId = ? ORDER BY Rating DESC", postId, parentCommentId)
                //    .Query<Comment>("SELECT * FROM comments WHERE PostId = ? ORDER BY Rating DESC", postId)
                //    .ToList();
                //return comments;
                return new Comment[] { new Comment { Text = "ONE" }, new Comment { Text = "TWO" }, new Comment { Text = "THREE" }, new Comment { Text = "FOUR" }, new Comment { Text = "FIVE" } }.ToList();
            });
        }

        public Task<Post> GetPostAsync(ID listId, int position)
        {
            return Task.Run(() =>
            {
                var sid = ToFlatId(listId);
                var p = MainDb.Instance.Query<Post>(
                    "SELECT * FROM posts WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)) LIMIT 1 OFFSET ?",
                    sid, position).First();
                if (Math.Abs(p.Timestamp - TimestampNow()) < Constants.PostListTime) return p;

                try
                {
                    //

                    var r = parsers.First(s => s.ParserId == listId.Site);
                    r.ExtractPost(p.PostId.Split('-')[1], state =>
                    {
                        switch (state.State)
                        {
                            case PostExportState.ExportState.Begin:
                                {
                                    //
                                    MainDb.Instance.Execute("DELETE FROM comments WHERE PostId = ?", p.Id);
                                }
                                break;
                            case PostExportState.ExportState.Info:
                                {
                                    // TODO
                                    MainDb.Instance.Execute("UPDATE posts SET Timestamp = ? WHERE Id = ?", TimestampNow(), p.Id);
                                }
                                break;
                            case PostExportState.ExportState.Comment:
                                {
                                    //
                                    var c = new Comment();
                                    c.CommentId = state.Comment.id;
                                    c.PostId = p.Id;
                                    c.Text = state.Comment.text;
                                    c.Created = state.Comment.created;
                                    c.UserName = state.Comment.userName;
                                    c.UserImage = state.Comment.userImage;
                                    c.Rating = state.Comment.rating;

                                    if (state.Comment.parentId != null)
                                    {
                                        c.ParentId = MainDb.Instance.ExecuteScalar<int>("SELECT Id FROM comments WHERE CommentId = ?", state.Comment.parentId);
                                    }

                                    MainDb.Instance.Insert(c);
                                }
                                break;
                        }
                    });

                }
                catch (Exception e)
                {
                    e.ToString();
                }

                return p;
            });

        }
        private static long TimestampNow()
        {
            return DateTime.Now.ToFileTimeUtc() / 10000L;
        }

        private string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }
    }
}