using System;
using System.Reactive.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model
{
	public class FeedService : FeedViewModel.IFeedService
	{
		internal static event EventHandler FeedChanged;

		IFeedRepository storage = ServiceLocator.Current.GetInstance<IFeedRepository>();
		ID id;

		public FeedService(ID id)
		{
			this.id = id;
		}

		public async Task ApplyNewItemsAsync()
		{
			await storage.ApplyNewItemsAsync(id);
			await InvalidateFeedAsync();
		}

		public IObservable<PostCollectionState> Get()
		{
			return Observable
				.FromAsync(() => storage.GetPostsAsync(id))
				.Merge(Observable.FromAsync(
					async () =>
					{
						await SyncPage(true);
						return await storage.GetPostsAsync(id);
					}));
		}

		public async Task ResetAsync()
		{
			await storage.ClearTagFromPostsAsync(id);
			await SyncPage(true);
		}

		public Task SyncNextPageAsync()
		{
			return SyncPage(false);
		}

		async Task SyncPage(bool isFirstPage)
		{
			var sorter = new OrderedListStorage(id, isFirstPage);
			await JoyReactorProvider.Create().LoadTagAndPostListAsync(id, sorter);
			await InvalidateFeedAsync();
		}

		internal static Task InvalidateFeedAsync()
		{
			TagCollectionModel.OnInvalidateEvent();
			return Task.Run(() => FeedChanged?.Invoke(null, null));
		}

		internal interface IFeedRepository
		{
			Task<PostCollectionState> GetPostsAsync(ID id);

			Task ApplyNewItemsAsync(ID id);

			Task ClearTagFromPostsAsync(ID id);
		}
	}
}