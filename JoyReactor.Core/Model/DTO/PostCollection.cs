using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.DTO
{
	public class PostCollection : List<Post>
	{
		public int NewItemsCount { get; set; }
		public int ActualCount { get; set; }
	}
}