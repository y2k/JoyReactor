using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Feed;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class PostService : PostViewModel.IPostService, CreateTagViewModel.IPostService
    {
        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        internal static event EventHandler PostChanged;
        int postId;

        internal PostService(int postId)
        {
            this.postId = postId;
        }

        public PostService() { }

        public IObservable<Post> Get()
        {
            SyncPost();
            return Observable
                .FromEventPattern(typeof(PostService), "PostChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => storage.GetPostWithAttachmentsAsync(postId)));
        }

        async void SyncPost()
        {
            var post = await storage.GetPostWithAttachmentsAsync(postId);
            await JoyReactorProvider.Create().LoadPostAsync(post.PostId);
            PostChanged?.Invoke(null, null);
        }

        public async Task CreateTagAsync(string name)
        {
            await storage.CreateMainTagAsync(name);
            TagCollectionModel.OnInvalidateEvent();
        }

        internal interface IStorage
        {
            Task<Post> GetPostWithAttachmentsAsync(int postId);

            Task CreateMainTagAsync(string name);
        }
    }
}