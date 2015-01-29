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

        public int ParentCommentId { get; set; }

        public string Text { get; set; }
        public long Created { get; set; }

        public float Rating { get; set; }

        public string UserName { get; set; }
        public string UserImage { get; set; }

        // TODO: придумать как заблокировать поле так что бы оно не игнорировалось при запросах
        public int ChildCount { get; set; }
        [Ignore]
        public List<string> Attachments { get; set; }

        public override string ToString()
        {
            return string.Format("Comment [Id = {0}, PostId = {1}, ParrentCommentId = {2}, ChildCount = {3}]", Id, PostId, ParentCommentId, ChildCount);
        }
    }
}