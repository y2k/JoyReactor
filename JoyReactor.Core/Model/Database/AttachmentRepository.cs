using JoyReactor.Core.Model.DTO;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Database
{
    class AttachmentRepository : Repository<Attachment>
    {
        public Task<List<Attachment>> GetAsync(int postId)
        {
            return Connection.QueryAsync<Attachment>(@"
                SELECT * 
                FROM attachments 
                WHERE ParentType = ? AND ParentId = ?",
                Attachment.ParentPost, postId);
        }

        public Task<List<Attachment>> GetForCommentsAsync(int postId)
        {
            return Connection.QueryAsync<Attachment>(@"
                SELECT * 
                FROM attachments 
                WHERE ParentType = ? AND ParentId IN (
                    SELECT Id
                    FROM comments
                    WHERE PostId = ?)",
                Attachment.ParentComment, postId);
        }
    }
}