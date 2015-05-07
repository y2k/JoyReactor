using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class PostService : PostViewModel.IPostService, CreateTagViewModel.IPostService
    {
        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        internal static event EventHandler PostChanged;
        int postId;

        public PostService() { }

        public PostService(int postId)
        {
            this.postId = postId;
        }

        public IObservable<List<Comment>> Get(int commentId)
        {
            return CreateEventObserver()
                .SelectMany(Observable.FromAsync(() => GetComments(commentId)));
        }

        async Task<List<Comment>> GetComments(int commentId)
        {
            var comments = await storage.GetChildCommentsAsync(postId, commentId);
            if (commentId != 0) comments.Insert(0, await storage.GetCommentAsync(commentId));
            return comments;
        }

        public IObservable<Post> Get()
        {
            SyncPost();
            return CreateEventObserver().SelectMany(Observable.FromAsync(GetPostAsync));
        }

        IObservable<EventPattern<object>> CreateEventObserver() {
            return Observable
                .FromEventPattern(typeof(PostService), "PostChanged")
                .StartWith((EventPattern<object>)null);
        }

        async Task<Post> GetPostAsync() {
            var post = await storage.GetPostAsync(postId);
            post.RelatedPosts = await storage.GetRelatedPostsAsync(postId);
            post.Attachments = await storage.GetAttachmentsAsync(postId);
            return post;
        }

        async void SyncPost()
        {
            var post = await storage.GetPostAsync(postId);
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
            Task<Post> GetPostAsync(int postId);

            Task<List<Attachment>> GetAttachmentsAsync(int postId);

            Task CreateMainTagAsync(string name);

            Task<List<RelatedPost>> GetRelatedPostsAsync(int postId);

            Task<List<Comment>> GetChildCommentsAsync(int postId, int commentId);

            Task<Comment> GetCommentAsync(int commentId);
        }
    }
}