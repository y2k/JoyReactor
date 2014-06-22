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

		public Task<List<Comment>> GetTopCommentsAsync (int postId, int count)
		{
			return Task.Run(() => {
				return MainDb.Instance
	                .SafeQuery<Comment>("SELECT * FROM comments WHERE PostId = ? AND ParentId = 0 ORDER BY Rating DESC LIMIT ?", postId, count)
	                .ToList();
			});
		}

        public Task<Post> GetPostAsync(ID listId, int position)
        {
            return Task.Run(() =>
            {
                var sid = ToFlatId(listId);
				Post p;
				lock (MainDb.Instance) {
					p = MainDb.Instance.Query<Post>(
	                    "SELECT * FROM posts WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)) LIMIT 1 OFFSET ?",
	                    sid, position).First();
				}
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
								lock (MainDb.Instance) {
                                    //
									MainDb.Instance.Execute("DELETE FROM comments WHERE PostId = ?", p.Id);
                                }
                                break;
                            case PostExportState.ExportState.Info:
								lock (MainDb.Instance) {
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

									if (state.Comment.parentId != null) {
										c.ParentId = MainDb.Instance.SafeQuery<Comment>("SELECT * FROM comments WHERE CommentId = ? AND PostId = ?", state.Comment.parentId, p.Id).First().Id;
                                    }
									MainDb.Instance.SafeInsert(c);
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

        public Task<List<string>> GetImages(int postId)
        {
            return Task.Run(() => {
                return MainDb.Instance
                    .SafeQuery<Comment>("SELECT * FROM comments WHERE PostId = ? AND ParentId = 0 ORDER BY Rating DESC LIMIT ?", postId, count)
                    .ToList();
            });
        }

        #region Private methods

        private static long TimestampNow()
        {
            return DateTime.Now.ToFileTimeUtc() / 10000L;
        }

        private string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }

        #endregion
    }
}