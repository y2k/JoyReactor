using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
using System;
using System.Collections.Generic;
using System.Reactive.Linq;

namespace JoyReactor.Core.Model
{
    class CommentService : PostViewModel.ICommentService
    {
        internal static CommentService Create(int postId)
        {
            return new CommentService();
        }

        public IObservable<List<Comment>> Get()
        {
            // TODO:
            return Observable.Empty<List<Comment>>();
        }
    }
}