using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.DTO
{
    [Table("comment_attachments")]
    public class CommentAttachment
    {
        public const int TypeImage = 0;
        public const int TypeYoutube = 1;
        public const int TypeCoub = 2;

        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int CommentId { get; set; }
        
        public int Type { get; set; }
        public string Url { get; set; }
    }
}
