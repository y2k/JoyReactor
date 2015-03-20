using SQLite.Net.Attributes;

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

        public int NextPage { get; set; }

        public override string ToString()
        {
            return string.Format("[Tag: Id={0}, Flags={1}, TagId={2}, Title={3}, BestImage={4}, Timestamp={5}, NextPage={6}]", Id, Flags, TagId, Title, BestImage, Timestamp, NextPage);
        }
    }
}