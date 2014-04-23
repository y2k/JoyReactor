using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
	public class TagPost
	{
		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }

		public int TagId { get; set; }

		public int PostId { get; set; }

		public int Status { get; set; }
	}
}