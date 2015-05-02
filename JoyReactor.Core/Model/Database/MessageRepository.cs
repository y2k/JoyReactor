using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Messages;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using RawMessage = JoyReactor.Core.Model.Messages.MessageFetcher.RawMessage;

namespace JoyReactor.Core.Model.Database
{
    public class MessageRepository : MessageFetcher.IStorage, MessageService.IStorage
    {
        SQLiteConnection db = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task<List<MessageThreadItem>> GetThreadsWithAdditionInformationAsync()
        {
            return db.QueryAsync<MessageThreadItem>(
                "SELECT * FROM ( " +
                "SELECT " +
                " t.Id AS Id, " +
                " t.UserName AS UserName, " +
                " t.UserImage AS UserImage, " +
                " m.Message AS LastMessage " +
                "FROM message_threads t " +
                "LEFT JOIN messages m ON t.Id = m.ThreadId " +
                "ORDER BY m.Created " +
                ") GROUP BY Id ");
        }

        public Task ClearAsync()
        {
            return db.RunInTransactionAsync(() =>
                {
                    db.Execute("DELETE FROM message_threads");
                    db.Execute("DELETE FROM messages");
                });
        }

        public Task SaveAsync(List<RawMessage> messages)
        {
            return db.RunInTransactionAsync(() =>
                {
                    SyncThreads(messages);
                    SyncMessages(messages);
                });
        }

        void SyncThreads(List<RawMessage> messages)
        {
            var threads = GetThreadsFromMessages(messages);
            foreach (var s in threads)
                db.Insert(s);
        }

        IEnumerable<PrivateMessageThread> GetThreadsFromMessages(List<RawMessage> messages)
        {
            return  from s in messages
                    group s by s.UserName into t
                    let thread = t.First()
                    select new PrivateMessageThread { UserName = thread.UserName, UserImage = thread.UserImage };
        }

        void SyncMessages(List<RawMessage> messages)
        {
            foreach (var s in messages)
                db.Insert(
                    new PrivateMessage
                    {
                        ThreadId = GetThreadForUser(s.UserName),
                        Created = s.Created,
                        Message = s.Message,
                        Mode = s.Mode == RawMessage.MessageMode.Outbox
						? PrivateMessage.ModeOutbox
						: PrivateMessage.ModeInbox,
                    });
        }

        int GetThreadForUser(string username)
        {
            return db.SafeExecuteScalar<int>("SELECT Id FROM message_threads WHERE UserName = ?", username);
        }

        public Task<List<PrivateMessage>> GetMessagesAsync(string username)
        {
            return db.QueryAsync<PrivateMessage>(
                "SELECT * " +
                "FROM messages " +
                "WHERE ThreadId IN ( " +
                "   SELECT Id " +
                "   FROM message_threads " +
                "   WHERE UserName = ?) " +
                "ORDER BY Created", 
                username);
        }

        public async Task PutMessageAsync(string username, PrivateMessage message)
        {
            message.ThreadId = GetThreadForUser(username);
            await db.InsertAsync(message);
        }
    }
}