using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public interface IPostModel
    {
        Task<List<Comment>> GetCommentsAsync(int postId, int parentCommentId);

		Task<List<Comment>> GetTopCommentsAsync(int postId, int count);

        Task<Post> GetPostAsync(int postId);

        Task<List<CommentAttachment>> GetAttachmentsAsync(int postId);
    }
}