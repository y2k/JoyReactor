using System.Collections.Generic;
using SQLite.Net.Attributes;

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

        public string _Attachments { get; set; }

        [Ignore]
        public ICollection<string> Attachments { 
            get { return _Attachments?.Split(',') ?? new string[0]; }
        }

        public override string ToString()
        {
            return string.Format("Comment [Id = {0}, PostId = {1}, ParrentCommentId = {2}, ChildCount = {3}]", Id, PostId, ParentCommentId, ChildCount);
        }
    }
}