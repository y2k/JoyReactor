using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("tag_post")]
    public class TagPost
    {
        public const int StatusOld = 1;
        public const int StatusActual = 2;

        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int TagId { get; set; }

        public int PostId { get; set; }

        public int Status { get; set; }

        public bool IsPending { get; set; }

        public override string ToString()
        {
            return string.Format("[TagPost: Id={0}, TagId={1}, PostId={2}, Status={3}, IsPending={4}]", Id, TagId, PostId, Status, IsPending);
        }

        internal TagPost Clone()
        {
            return (TagPost)MemberwiseClone();
        }
    }
}