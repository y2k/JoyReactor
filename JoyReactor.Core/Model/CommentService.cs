using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class CommentService
    {
        internal static CommentService Create(int postId)
        {
            return new CommentService();
        }

        internal IObservable<List<Comment>> Get()
        {
            throw new NotImplementedException();
        }
    }
}