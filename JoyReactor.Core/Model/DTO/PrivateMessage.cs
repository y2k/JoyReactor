using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
    [Table("messages")]
    public class PrivateMessage
    {
        public const int ModeInbox = 0;
        public const int ModeOutbox = 1;

        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }

        public int ThreadId { get; set; }

        public string Message { get; set; }

        public DateTime Created { get; set; }

        public int Mode { get; set; }

        public override string ToString()
        {
            return string.Format("[PrivateMessage: Id={0}, ThreadId={1}, Message={2}, Created={3}, Mode={4}]", Id, ThreadId, Message, Created, Mode);
        }
    }
}