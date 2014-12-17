using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("attachments")]
    public class Attachment
    {
        public const int TypeImage = 0;
        public const int TypeYoutube = 1;
        public const int TypeCoub = 2;

        public const int ParentPost = 0;
        public const int ParentComment = 1;

        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int ParentType { get; set; }
        public int ParentId { get; set; }

        public int Type { get; set; }
        public string Url { get; set; }

        public string PreviewImageUrl { get; set; }
        public int PreviewImageWidth { get; set; }
        public int PreviewImageHeight { get; set; }
    }
}