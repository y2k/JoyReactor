using System;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.DTO;

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
				.FromAsync(() => GetPostsAsync(id))
				.Merge(Observable.FromAsync(
                    async () =>
                    {
                        await SyncPage(true);
                        return await GetPostsAsync(id);
                    }));
        }

        async Task<PostCollectionState> GetPostsAsync(ID id)
        {
            var result = new PostCollectionState();

            var tag = await new TagRepository().GetAsync(id.SerializeToString());
            var tagPosts = await new TagPostRepository().GetAllAsync(tag.Id);
            var posts = await new PostRepository().GetAllAsync(tag.Id);

            result.Posts = tagPosts
                .Where(s => s.Status != null)
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
            var sorter = ListStorageFactory.NewInstance(id, isFirstPage);
            await JoyReactorProvider.Create().LoadTagAndPostListAsync(id, sorter, isFirstPage);
            await InvalidateFeedAsync();
        }

        internal static Task InvalidateFeedAsync()
        {
            TagCollectionModel.OnInvalidateEvent();
            return Task.Run(() => FeedChanged?.Invoke(null, null));
        }

        internal interface IFeedRepository
        {
            //            Task<PostCollectionState> GetPostsAsync(ID id);

            Task ApplyNewItemsAsync(ID id);

            Task ClearTagFromPostsAsync(ID id);
        }
    }
}