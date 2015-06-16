using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Messages;
using RawMessage = JoyReactor.Core.Model.Messages.MessageFetcher.RawMessage;

namespace JoyReactor.Core.Model.Database
{
    class MessageRepository : Repository<object>, MessageFetcher.IStorage, MessageService.IStorage
    {
        public Task<List<MessageThreadItem>> GetThreadsWithAdditionInformationAsync()
        {
            return Connection.QueryAsync<MessageThreadItem>(
                "SELECT * FROM ( " +
                "SELECT " +
                " t.Id AS Id, " +
                " t.UserName AS UserName, " +
                " t.UserImage AS UserImage, " +
                " m.Created AS LastMessageTime, " +
                " m.Message AS LastMessage " +
                "FROM message_threads t " +
                "LEFT JOIN messages m ON t.Id = m.ThreadId " +
                "ORDER BY m.Created " +
                ") GROUP BY Id ");
        }

        public async Task ClearAsync()
        {
            await Connection.ExecuteAsync("DELETE FROM message_threads");
            await Connection.ExecuteAsync("DELETE FROM messages");
        }

        public async Task SaveAsync(List<RawMessage> messages)
        {
            await SyncThreads(messages);
            await SyncMessages(messages);
        }

        async Task SyncThreads(List<RawMessage> messages)
        {
            var threads = GetThreadsFromMessages(messages);
            foreach (var s in threads)
                await Connection.InsertAsync(s);
        }

        IEnumerable<PrivateMessageThread> GetThreadsFromMessages(List<RawMessage> messages)
        {
            return from s in messages
                   group s by s.UserName into t
                   let thread = t.First()
                   select new PrivateMessageThread { UserName = thread.UserName, UserImage = thread.UserImage };
        }

        async Task SyncMessages(List<RawMessage> messages)
        {
            foreach (var s in messages)
                await Connection.InsertAsync(
                    new PrivateMessage
                    {
                        ThreadId = await GetThreadForUser(s.UserName),
                        Created = s.Created,
                        Message = s.Message,
                        Mode = s.Mode == RawMessage.MessageMode.Outbox
                        ? PrivateMessage.ModeOutbox
                        : PrivateMessage.ModeInbox,
                    });
        }

        public Task<List<PrivateMessage>> GetMessagesAsync(string username)
        {
            return Connection.QueryAsync<PrivateMessage>(
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
            message.ThreadId = await GetThreadForUser(username);
            await Connection.InsertAsync(message);
        }

        Task<int> GetThreadForUser(string username)
        {
            return Connection.ExecuteScalarAsync<int>("SELECT Id FROM message_threads WHERE UserName = ?", username);
        }
    }
}