using System;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
	public interface IPostCollectionModel
	{
//		event EventHandler<ID> PostChanged;
//
//		void NotifyPostsChanged();

		Task SyncTask(ID id, SyncFlags flags);

		Task<List<Post>> GetPostsAsync (ID id);

		[Obsolete]
		Task<List<Post>> GetPostsAsync(ID id, SyncFlags flags = SyncFlags.None);

		int GetCount (ID id);
    
        Task<int> GetCountAsync(ID id);

		Task<PostCollection> GetListAsync(ID id, SyncFlags flags = SyncFlags.None);
    }

	public enum SyncFlags { None, Next, First }
}