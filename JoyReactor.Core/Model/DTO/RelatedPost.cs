using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("related_posts")]
    public class RelatedPost
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int ParentPost { get; set; }

        public string Image { get; set; }
    }
}