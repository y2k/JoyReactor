using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class PostModel
    {
        SQLiteConnection db = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task<List<Comment>> GetCommentsAsync(int postId, int parentCommentId)
        {
            if (parentCommentId == 0)
                return GetTopCommentsAsync(postId, int.MaxValue);
            throw new NotImplementedException();
        }

        public Task<List<Comment>> GetTopCommentsAsync(int postId, int count)
        {
            //			return Task.Run (() => {
            //				return connection
            //                    .SafeQuery<Comment> ("SELECT * FROM comments WHERE PostId = ? AND ParentId = 0 ORDER BY Rating DESC LIMIT ?", postId, count)
            //                    .ToList ();
            //			});
            return Task.Run(() =>
            {
                return db
                    .SafeQuery<Comment>("SELECT * FROM comments WHERE PostId = ? AND Id NOT IN (SELECT CommentId FROM comment_links) ORDER BY Rating DESC LIMIT ?", postId, count);
            });
        }

        public Task<Post> GetPostAsync(int postId)
        {
            return Task.Run(() =>
            {
                var p = db.SafeQuery<Post>("SELECT * FROM posts WHERE Id = ?", postId).First();
                if (Math.Abs(p.Timestamp - TimestampNow()) < Constants.PostListTime)
                    return p;

                try
                {
                    var r = GetSiteParserForPost(p);
                    var syncId = p.PostId.Split('-')[1];

                    r.NewPostInformation += (sender, state) =>
                    {
                        db.SafeExecute("DELETE FROM attachments WHERE ParentType = ? AND ParentId = ?", Attachment.ParentPost, p.Id);
                        db.SafeExecute("DELETE FROM attachments WHERE ParentType = ? AND ParentId IN (SELECT Id FROM comments WHERE PostId = ?)", Attachment.ParentComment, p.Id);
                        db.SafeExecute("DELETE FROM comment_links WHERE CommentId IN (SELECT Id FROM comments WHERE PostId = ?)", p.Id);
                        db.SafeExecute("DELETE FROM comments WHERE PostId = ?", p.Id);

                        db.SafeExecute("UPDATE posts SET Timestamp = ? WHERE Id = ?", TimestampNow(), p.Id);
                        db.SafeExecute("UPDATE posts SET Content = ? WHERE Id = ?", state.Content, p.Id);

                        foreach (var a in state.Attachments)
                            db.SafeInsert(new Attachment
                            {
                                ParentType = Attachment.ParentPost,
                                ParentId = p.Id,
                                Type = Attachment.TypeImage,
                                Url = a.Image,
                                PreviewImageUrl = a.Image,
                                PreviewImageWidth = a.Width,
                                PreviewImageHeight = a.Height,
                            });
                    };
                    r.NewComment += (sender, state) =>
                    {
                        var c = new Comment();
                        c.CommentId = state.Id;
                        c.PostId = p.Id;
                        c.Text = state.Content;
                        c.Created = state.Created.ToUnixTimestamp();
                        c.UserName = state.User.Name;
                        c.UserImage = state.User.Avatar;
                        c.Rating = state.Rating;

                        //							if (state.Comment.parentId != null) {
                        //								c.ParentId = connection.SafeQuery<Comment> ("SELECT * FROM comments WHERE CommentId = ? AND PostId = ?", state.Comment.parentId, p.Id).First ().Id;
                        //							}

                        db.SafeInsert(c);
                        foreach (var i in state.ParentIds)
                            db.SafeExecute(
                                "INSERT INTO comment_links (CommentId, ParentCommentId) " +
                                "SELECT ?, Id FROM comments WHERE CommentId = ?", c.Id, i);

                        foreach (var a in state.Attachments)
                            db.SafeInsert(new Attachment
                            {
                                ParentType = Attachment.ParentComment,
                                ParentId = c.Id,
                                Type = Attachment.TypeImage,
                                Url = a.Image,
                            });
                    };
                    r.ExtractPost(syncId);

                }
                catch (Exception e)
                {
                    throw e;
                }

                p = db.SafeQuery<Post>("SELECT * FROM posts WHERE Id = ?", postId).First();
                return p;
            });
        }

        SiteParser GetSiteParserForPost(Post p)
        {
            var parsers = ServiceLocator.Current.GetInstance<SiteParser[]>();
            var parserId = (ID.SiteParser)Enum.Parse(typeof(ID.SiteParser), p.PostId.Split('-')[0]);
            return parsers.First(s => s.ParserId == parserId);
        }


        public Task<List<Attachment>> GetPostAttachmentsAsync(int postId)
        {
            return Task.Run(() =>
            {
                var sql = "SELECT * FROM attachments WHERE ParentType = ? AND ParentId = ?";
                return db.SafeQuery<Attachment>(sql, Attachment.ParentPost, postId).ToList();
            });
        }

        public Task<List<Attachment>> GetAttachmentsAsync(int postId)
        {
            return Task.Run(() =>
            {
                var sql = "SELECT * FROM attachments WHERE ParentType = ? AND ParentId IN (SELECT Id FROM comments WHERE PostId = ?)";
                return db.SafeQuery<Attachment>(sql, Attachment.ParentComment, postId).ToList();
            });
        }

        public Task CreateTag(string name)
        {
            return Task.Run(() =>
            {
                db.SafeInsert(new Tag
                {
                    TagId = ToFlatId(ID.Factory.NewTag(name.ToLower())),
                    Title = name,
                    Flags = Tag.FlagShowInMain,
                });
            });
        }

        #region Private methods

        static long TimestampNow()
        {
            return DateTime.Now.ToFileTimeUtc() / 10000L;
        }

        string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }

        #endregion
    }
}