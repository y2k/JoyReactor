using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
	[Table("tag_post")]
	public class TagPost
	{
		public const int StatusComplete = 0;
		public const int StatusNew = 1;

		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }

		public int TagId { get; set; }

		public int PostId { get; set; }

		public int Status { get; set; }
	}
}