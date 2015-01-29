using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class CommentService : PostViewModel.ICommentService
    {
        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        int postId;

        internal CommentService(int postId)
        {
            this.postId = postId;
        }

        public IObservable<List<Comment>> Get(int commentId)
        {
            return Observable
                .FromEventPattern(typeof(PostService), "PostChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => GetComments(commentId)));
        }

        async Task<List<Comment>> GetComments(int commentId)
        {
            var comments = await storage.GetChildCommentsAsync(postId, commentId);
            if (commentId != 0) comments.Insert(0, await storage.GetCommentAsync(commentId));
            return comments;
        }

        internal interface IStorage
        {
            Task<List<Comment>> GetChildCommentsAsync(int postId, int commentId);

            Task<Comment> GetCommentAsync(int commentId);
        }
    }
}