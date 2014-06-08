using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.DTO
{
    [Table("comments")]
    class Comment
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }
        public int ParentId { get; set; }

        public int PostId { get; set; }

        public String CommentId;

        public String Text { get; set; }
        public long Created { get; set; }

        public float Rating { get; set; }

        public String UserName { get; set; }
        public String UserImage { get; set; }
    }
}
