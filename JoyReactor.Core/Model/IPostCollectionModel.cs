using System;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
	public interface IPostCollectionModel
	{
		Task<List<Post>> GetPostsAsync(ID id, SyncFlags flags = SyncFlags.None);
	}

	public enum SyncFlags { None, Next, First }
}