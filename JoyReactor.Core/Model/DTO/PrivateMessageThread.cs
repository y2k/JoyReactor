using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
	[Table ("message_threads")]
	public class PrivateMessageThread
	{
		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }

		[Unique]
		public string UserName { get; set; }

		public string UserImage { get; set; }
	}
}