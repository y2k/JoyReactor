using JoyReactor.Core.Model.Parser;
using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Feed
{
    class FeedService
    {
        internal static event EventHandler FeedChanged;

        IStorage storage;
        IFeedProvider provider;

        internal IObservable<PostCollectionState> Get(ID id)
        {
            UpdateAsync(id);

            return Observable
                .FromEventPattern(typeof(FeedService), "FeedChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => storage.GetPostsAsync(id)));
        }

        private async void UpdateAsync(ID id)
        {
            await provider.UpdateAsync(id);
            FeedChanged?.Invoke(null, null);
        }

        internal interface IStorage
        {
            Task<PostCollectionState> GetPostsAsync(ID id);
        }

        internal interface IFeedProvider
        {
            Task UpdateAsync(ID id);
        }
    }
}