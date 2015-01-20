using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Feed
{
    class FeedService : IFeedService
    {
        internal static event Action FeedChanged;

        IStorage storage;
        IFeedProvider provider;

        public IObservable<PostCollectionState> Get(ID id)
        {
            UpdateAsync(id);

            return Observable
                .FromEventPattern(typeof(FeedService), "FeedChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => storage.GetPostsAsync(id)));
        }

        private async void UpdateAsync(ID id)
        {
            await provider.UpdateFirstPageAsync(id);
            InvalidateFeed();
        }

        public async Task ApplyNewItemsAsync(ID id)
        {
            await storage.ApplyNewItemsAsync(id);
            InvalidateFeed();
        }

        public async Task ResetAsync(ID id)
        {
            await storage.ClearTagFromPostsAsync(id);
            await provider.UpdateFirstPageAsync(id);
            InvalidateFeed();
        }

        public async Task LoadNextPage(ID id)
        {
            await provider.UpdateNextPageAsync(id);
            InvalidateFeed();
        }

        private static void InvalidateFeed()
        {
            FeedChanged?.Invoke();
        }

        internal interface IStorage
        {
            Task<PostCollectionState> GetPostsAsync(ID id);

            Task ApplyNewItemsAsync(ID id);
            Task ClearTagFromPostsAsync(ID id);
        }

        internal interface IFeedProvider
        {
            Task UpdateFirstPageAsync(ID id);

            Task UpdateNextPageAsync(ID id);
        }
    }
}