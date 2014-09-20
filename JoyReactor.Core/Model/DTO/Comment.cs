using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.DTO
{
    [Table("comments")]
    public class Comment
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int PostId { get; set; }

        public String CommentId { get; set; }

        public String Text { get; set; }
        public long Created { get; set; }

        public float Rating { get; set; }

        public String UserName { get; set; }
        public String UserImage { get; set; }
    }
}
