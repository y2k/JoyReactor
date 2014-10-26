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

		[Obsolete]
		Task SyncTask(ID id, SyncFlags flags);

		[Obsolete]
		Task<List<Post>> GetPostsAsync (ID id);

		[Obsolete]
		Task<List<Post>> GetPostsAsync(ID id, SyncFlags flags = SyncFlags.None);

		[Obsolete]
		int GetCount (ID id);
    
		[Obsolete]
        Task<int> GetCountAsync(ID id);

		[Obsolete]
		Task<PostCollection> GetListAsync(ID id, SyncFlags flags = SyncFlags.None);

		Task<PostCollectionState> Get (ID id);

		Task SyncFirstPage(ID id);

		Task ApplyNewItems(ID id);
    }

	public class PostCollectionState
	{
		public List<Post> Posts { get; set; }
		public int NewItemsCount { get; set; }
	}

	public enum SyncFlags { None, Next, First }
}