using SQLite.Net.Attributes;
using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.DTO
{
    [Table("comments")]
    public class Comment
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int PostId { get; set; }

        public string CommentId { get; set; }

        public string Text { get; set; }
        public long Created { get; set; }

        public float Rating { get; set; }

        public string UserName { get; set; }
        public string UserImage { get; set; }

        [Ignore]
        public int ParentCommentId { get; set; }
        [Ignore]
        public int ChildCount { get; set; }
        [Ignore]
        public List<string> Attachments { get; set; }
    }
}