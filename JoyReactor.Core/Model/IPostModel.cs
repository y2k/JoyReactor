using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public interface IPostModel
    {
        Task<List<Comment>> GetCommentsAsync(int postId, int parentCommentId);

        Task<Post> GetPostAsync(ID listId, int position);
    }
}