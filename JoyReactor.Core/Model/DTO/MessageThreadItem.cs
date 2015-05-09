using System;

namespace JoyReactor.Core.Model.DTO
{
    public class MessageThreadItem
    {
        public string UserName { get; set; }

        public string UserImage { get; set; }

        public string LastMessage { get; set; }

        public DateTime LastMessageTime { get; set; }
    }
}