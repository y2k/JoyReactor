using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class FeedService : FeedViewModel.IFeedService
    {
        const int IgnoreResult = 0;

        //internal static event EventHandler FeedChanged;

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
                .Return(IgnoreResult)
                //.Merge(Observable.FromEventPattern(s => FeedChanged += s, s => FeedChanged -= s).Select(_ => IgnoreResult))
                .Merge(Observable.FromAsync(() => SyncPage(true)).Select(_ => IgnoreResult))
                .SelectMany(_ => Observable.FromAsync(() => GetPostsAsync(id)));
        }

        async Task<PostCollectionState> GetPostsAsync(ID id)
        {
            var result = new PostCollectionState();

            var tag = await new TagRepository().GetAsync(id.SerializeToString());
            var tagPosts = await new TagPostRepository().GetAllAsync(tag.Id);
            var posts = await new PostRepository().GetAllAsync(tag.Id);

            result.Posts = tagPosts
                .Where(s => s.Status != 0)
                .Join(posts, s => s.PostId, s => s.Id, (t, p) => p)
                .ToList();
            result.NewItemsCount = tagPosts.Count(s => s.Status == 0 && s.IsPending);
            result.DividerPosition = tagPosts.Count(s => s.Status == TagPost.StatusActual);

            return result;
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
            await new PostCollectionLoader(id, isFirstPage).LoadAsync();
            await InvalidateFeedAsync();
        }

        internal static Task InvalidateFeedAsync()
        {
//            TagCollectionModel.InvalidateTagCollection();
//            return Task.Run(() => FeedChanged?.Invoke(null, null));

            throw new NotImplementedException();
        }

        internal interface IFeedRepository
        {
            Task ApplyNewItemsAsync(ID id);

            Task ClearTagFromPostsAsync(ID id);
        }
    }
}