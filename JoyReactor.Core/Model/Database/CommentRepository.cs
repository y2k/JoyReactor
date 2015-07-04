using System.Collections.Generic;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;
using System.Linq;

namespace JoyReactor.Core.Model.Database
{
    class CommentRepository : Repository<Comment>
    {
        public Task<List<Comment>> GetChildCommentsAsync(int postId, int commentId)
        {
            return Connection.QueryAsync<Comment>(@"
                SELECT
                c.*,
                (SELECT COUNT(*) FROM comments WHERE ParentCommentId = c.Id) AS ChildCount,
                a.Url AS _Attachments
                FROM comments c
                LEFT JOIN attachments a ON a.ParentType == 1 AND a.ParentId = c.Id
                WHERE c.PostId = ? AND c.ParentCommentId = ?
                GROUP BY c.Id
                ORDER BY c.Rating DESC, ChildCount DESC
                ", postId, commentId);
        }

        public async Task<Comment> GetCommentAsync(int commentId)
        {
            var comments = await Connection.QueryAsync<Comment>(@"
                SELECT  
                c.*, 
                (
                    SELECT COUNT(*) 
                    FROM comments 
                    WHERE ParentCommentId = c.Id
                ) AS ChildCount 
                FROM comments c 
                WHERE c.Id = ?
                ", commentId);
            return comments.First();
        }
    }
}