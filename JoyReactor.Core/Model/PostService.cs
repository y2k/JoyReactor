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

        internal IObservable<Post> GetInformation()
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}