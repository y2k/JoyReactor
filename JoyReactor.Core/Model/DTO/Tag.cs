using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
	[Table("tags")]
	public class Tag
	{
		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }

		public bool ShowInMain { get; set; }
		public string Title { get; set; }
		public string BestImage { get; set; }
	}
}