using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;

namespace JoyReactor.Core.Model
{
    public class PostCollectionState
    {
        internal List<Post> Posts { get; set; }

        internal int NewItemsCount { get; set; }

        internal int DividerPosition { get; set; }
    }
}