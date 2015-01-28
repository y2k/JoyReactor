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
        internal event EventHandler PostChanged;
        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        int postId;

        internal CommentService(int postId)
        {
            this.postId = postId;
        }

        public IObservable<List<Comment>> Get()
        {
            return Observable
                .FromEventPattern(this, "PostChanged")
                .StartWith((EventPattern<object>)null)
                .SelectMany(Observable.FromAsync(() => storage.GetCommentsAsync()));
        }

        internal interface IStorage
        {
            Task<List<Comment>> GetCommentsAsync();
        }
    }
}