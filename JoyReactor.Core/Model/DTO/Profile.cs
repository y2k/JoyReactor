using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("profiles")]
	public class Profile
	{
		[PrimaryKey]
		public string Site { get; set; }

		public string UserName { get; set; }
		public string Cookie { get; set; }
        public Uri UserImage { get; internal set; }
        public int Stars { get; internal set; }
        public float NextStarProgress { get; internal set; }
        public float Rating { get; internal set; }
    }
}