using JoyReactor.Core.Model.DTO;
using SQLite.Net;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class CreateTablesTransaction
    {
        AsyncSQLiteConnection db;

        public CreateTablesTransaction(AsyncSQLiteConnection db)
        {
            this.db = db;
        }

        public async Task Execute()
        {
            await db.CreateTableAsync<Post>();
            await db.CreateTableAsync<Tag>();
            await db.CreateTableAsync<TagPost>();
            await db.CreateTableAsync<TagLinkedTag>();
            await db.CreateTableAsync<Comment>();
            await db.CreateTableAsync<Attachment>();
            await db.CreateTableAsync<Profile>();
            await db.CreateTableAsync<PrivateMessageThread>();
            await db.CreateTableAsync<PrivateMessage>();
            await db.CreateTableAsync<RelatedPost>();
        }
    }
}