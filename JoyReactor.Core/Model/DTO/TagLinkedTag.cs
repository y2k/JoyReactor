using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("tag_linked_tags")]
	public class TagLinkedTag
	{
		[PrimaryKey, AutoIncrement]
		public int Id { get; set; }
		public int ParentTagId { get; set; }
		public string TagId { get; set; }
		public string Title { get; set; }
		public string Image { get; set; }
		public string GroupName { get; set; }
	}
}