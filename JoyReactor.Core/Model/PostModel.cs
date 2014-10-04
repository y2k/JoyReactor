using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Core.Model
{
	class PostModel : IPostModel
	{
		public Task<List<Comment>> GetCommentsAsync (int postId, int parentCommentId)
		{
			return Task.Run (() => {
				//var comments = MainDb.Instance
				//    //.Query<Comment>("SELECT * FROM comments WHERE PostId = ? AND ParentId = ? ORDER BY Rating DESC", postId, parentCommentId)
				//    .Query<Comment>("SELECT * FROM comments WHERE PostId = ? ORDER BY Rating DESC", postId)
				//    .ToList();
				//return comments;
				return new Comment[] {
					new Comment { Text = "ONE" },
					new Comment { Text = "TWO" },
					new Comment { Text = "THREE" },
					new Comment { Text = "FOUR" },
					new Comment { Text = "FIVE" }
				}.ToList ();
			});
		}

		public Task<List<Comment>> GetTopCommentsAsync (int postId, int count)
		{
//			return Task.Run (() => {
//				return MainDb.Instance
//                    .SafeQuery<Comment> ("SELECT * FROM comments WHERE PostId = ? AND ParentId = 0 ORDER BY Rating DESC LIMIT ?", postId, count)
//                    .ToList ();
//			});
			return Task.Run (() => {
				return MainDb.Instance
					.SafeQuery<Comment> ("SELECT * FROM comments WHERE PostId = ? AND Id NOT IN (SELECT CommentId FROM comment_links) ORDER BY Rating DESC LIMIT ?", postId, count)
                    .ToList ();
			});
		}

		public Task<Post> GetPostAsync (int postId)
		{
			return Task.Run (() => {
				var p = MainDb.Instance.SafeQuery<Post> ("SELECT * FROM posts WHERE Id = ?", postId).First ();
				if (Math.Abs (p.Timestamp - TimestampNow ()) < Constants.PostListTime)
					return p;

				try {
					var r = GetSiteParserForPost (p);
					var syncId = p.PostId.Split ('-') [1];

					r.NewPost += (sender, state) => {
						MainDb.Instance.SafeExecute ("DELETE FROM comment_attachments WHERE CommentId IN (SELECT Id FROM comments WHERE PostId = ?)", p.Id);
						MainDb.Instance.SafeExecute ("DELETE FROM comment_links WHERE CommentId IN (SELECT Id FROM comments WHERE PostId = ?)", p.Id);
						MainDb.Instance.SafeExecute ("DELETE FROM comments WHERE PostId = ?", p.Id);

						MainDb.Instance.SafeExecute ("UPDATE posts SET Timestamp = ? WHERE Id = ?", TimestampNow (), p.Id);
						MainDb.Instance.SafeExecute ("UPDATE posts SET Content = ? WHERE Id = ?", state.Content, p.Id);
					};
					r.NewComment += (sender, state) => {
						var c = new Comment ();
						c.CommentId = state.Id;
						c.PostId = p.Id;
						c.Text = state.Content;
						c.Created = state.Created.ToUnixTimestamp ();
						c.UserName = state.User.Name;
						c.UserImage = state.User.Avatar;
						c.Rating = state.Rating;

						//							if (state.Comment.parentId != null) {
						//								c.ParentId = MainDb.Instance.SafeQuery<Comment> ("SELECT * FROM comments WHERE CommentId = ? AND PostId = ?", state.Comment.parentId, p.Id).First ().Id;
						//							}
						var cid = MainDb.Instance.SafeInsert (c);

						var comIds = state.ParentIds ?? new string[0];
						foreach (var i in comIds) {
							MainDb.Instance.SafeExecute (
								"INSERT INTO comment_links (CommentId, ParentCommentId) " +
								"SELECT ?, Id FROM comments WHERE CommentId = ?", cid, i); 
						}

						if (state.Attachments != null)
							foreach (var a in state.Attachments) {
								var pa = new CommentAttachment ();
								pa.CommentId = c.Id;
								pa.Type = CommentAttachment.TypeImage;
								pa.Url = a.Image;
								MainDb.Instance.SafeInsert (pa);
							}
					};
				} catch (Exception e) {
					Log.Error (e);
				}

				p = MainDb.Instance.SafeQuery<Post> ("SELECT * FROM posts WHERE Id = ?", postId).First ();
				return p;
			});
		}

		private SiteParser GetSiteParserForPost (Post p)
		{
			var parsers = ServiceLocator.Current.GetInstance<SiteParser[]> ();
			var parserId = (ID.SiteParser)Enum.Parse (typeof(ID.SiteParser), p.PostId.Split ('-') [0]);
			return parsers.First (s => s.ParserId == parserId);
		}

		public Task<List<CommentAttachment>> GetAttachmentsAsync (int postId)
		{
			return Task.Run (() => {
				return MainDb.Instance
                    .SafeQuery<CommentAttachment> ("SELECT * FROM comment_attachments WHERE CommentId IN (SELECT Id FROM comments WHERE PostId = ?)", postId)
                    .ToList ();
			});
		}

		#region Private methods

		private static long TimestampNow ()
		{
			return DateTime.Now.ToFileTimeUtc () / 10000L;
		}

		private string ToFlatId (ID id)
		{
			return id.Site + "-" + id.Type + "-" + id.Tag;
		}

		#endregion
	}
}