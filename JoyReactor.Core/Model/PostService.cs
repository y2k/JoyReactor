using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class PostService
    {
        int postId;

        #region New instance factory

        PostService() { }

        internal static PostService Create(int postId)
        {
            return new PostService() { postId = postId };
        }

        internal static PostService Create()
        {
            return new PostService();
        }

        #endregion

        internal IObservable<Post> Get()
        {
            throw new NotImplementedException();
        }

        internal Task CreateTagAsync(string name)
        {
            throw new NotImplementedException();
        }
    }
}