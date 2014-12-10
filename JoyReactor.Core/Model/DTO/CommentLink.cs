using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("comment_links")]
	public class CommentLink
	{
		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }

		public int CommentId { get; set; }

		public int ParentCommentId { get; set; }
	}
}