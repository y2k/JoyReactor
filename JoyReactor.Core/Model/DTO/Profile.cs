using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("profiles")]
    public class Profile
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public string UserName { get; set; }

        public string UserImage { get; set; }

        public string Cookie { get; set; }

        public int Stars { get; set; }

        public float NextStarProgress { get; set; }

        public float Rating { get; set; }
    }
}