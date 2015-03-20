using System.Collections.Generic;

namespace JoyReactor.Core.Model.DTO
{
    public class TagGroup
    {
        public ICollection<Tag> Tags { get; set; }

        public string Title { get; set; }
    }
}