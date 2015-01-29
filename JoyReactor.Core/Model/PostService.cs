using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class PostService : PostViewModel.IPostService
    {
        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        internal static event EventHandler PostChanged;
        int postId;

        internal PostService(int postId)
        {
            this.postId = postId;
        }

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

        internal interface IStorage
        {
            Task<Post> GetPostWithAttachmentsAsync(int postId);
        }
    }
}