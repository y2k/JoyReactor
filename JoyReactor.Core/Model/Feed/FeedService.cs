using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;
using System.Threading;

namespace JoyReactor.Core.Model.Feed
{
    public class FeedService : FeedViewModel.IFeedService
    {
        internal static event EventHandler FeedChanged;

        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        ID id;

        #region Instance factory

        FeedService() { }

        internal static FeedService Create(ID id)
        {
            return new FeedService { id = id };
        }

        #endregion

        public async Task ApplyNewItemsAsync()
        {
            await storage.ApplyNewItemsAsync(id);
            await InvalidateFeedAsync();
        }

        public IObservable<PostCollectionState> Get()
        {
#pragma warning disable CS4014
            SyncPage(true);
#pragma warning restore CS4014
            return Observable
                .FromEventPattern(typeof(FeedService), "FeedChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => storage.GetPostsAsync(id)));
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

        private async Task SyncPage(bool isFirstPage)
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

        internal interface IStorage
        {
            Task<PostCollectionState> GetPostsAsync(ID id);

            Task ApplyNewItemsAsync(ID id);

            Task ClearTagFromPostsAsync(ID id);
        }
    }
}