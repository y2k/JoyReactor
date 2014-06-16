using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System;

namespace JoyReactor.Core.Model.DTO
{
	[Table("tags")]
	public class Tag
	{
		public const int FlagSystem = 1;
		public const int FlagShowInMain = 2;
		public const int FlagWebRead = 4;

		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }
		public int Flags { get; set; }
		public string TagId { get; set; }
		public string Title { get; set; }
		public string BestImage { get; set; }
        public long Timestamp { get; set; }
	}
}