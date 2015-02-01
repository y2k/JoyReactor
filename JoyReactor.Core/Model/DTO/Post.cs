using SQLite.Net.Attributes;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.DTO
{
    [Table("posts")]
    public class Post
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        //[Unique]
        public string PostId { get; set; }

        public string Image { get; set; }

        public int ImageWidth { get; set; }

        public int ImageHeight { get; set; }

        public string UserName { get; set; }

        public string Title { get; set; }

        public long Created { get; set; }

        public string UserImage { get; set; }

        public float Rating { get; set; }

        public string Coub { get; set; }

        public int CommentCount { get; set; }

        public long Timestamp { get; set; }

        public string Content { get; set; }

        [Ignore]
        public List<Attachment> Attachments { get; set; }

        [Ignore]
        public List<RelatedPost> RelatedPosts { get; set; }
    }
}