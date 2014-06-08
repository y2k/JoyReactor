using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class PostModel : IPostModel
    {
        public Task<List<string>> GetCommentsAsync(int postId, int parentCommentId)
        {
            return Task.Run<List<string>>(() =>
            {
                var list = new List<string>();
                for (int i = 0; i < 100; i++)
                {
                    list.Add("" + DateTime.Now);
                }
                return list;
            });
        }

        public Task<Post> GetPostAsync(ID listId, int position)
        {
            return Task.Run(
                () =>
                {
                    var sid = ToFlatId(listId);
                    return MainDb.Instance.Query<Post>(
                        "SELECT * FROM posts WHERE Id IN (SELECT PostId FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?)) LIMIT 1 OFFSET ?",
                        sid, position).First();
                });

        }

        private string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }
    }
}