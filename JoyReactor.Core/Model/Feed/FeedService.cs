using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Feed
{
    class FeedService
    {
        ID id;

        #region Instance factory

        FeedService() { }

        internal static FeedService Create(ID id)
        {
            return new FeedService { id = id };
        }

        #endregion

        internal static event EventHandler FeedChanged;

        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        IFeedProvider provider = ServiceLocator.Current.GetInstance<IFeedProvider>();

        public IObservable<PostCollectionState> Get()
        {
            RequestSyncFeedInBackgroundThread();

            return Observable
                .FromEventPattern(typeof(FeedService), "FeedChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => storage.GetPostsAsync(id)));
        }

        private async void RequestSyncFeedInBackgroundThread()
        {
            await provider.UpdateFirstPageAsync(id);
            InvalidateFeed();
        }

        public async Task ApplyNewItemsAsync()
        {
            await storage.ApplyNewItemsAsync(id);
            InvalidateFeed();
        }

        public async Task ResetAsync()
        {
            await storage.ClearTagFromPostsAsync(id);
            await provider.UpdateFirstPageAsync(id);
            InvalidateFeed();
        }

        public async Task LoadNextPage()
        {
            await provider.UpdateNextPageAsync(id);
            InvalidateFeed();
        }

        private static void InvalidateFeed()
        {
            FeedChanged?.Invoke(null, null);
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