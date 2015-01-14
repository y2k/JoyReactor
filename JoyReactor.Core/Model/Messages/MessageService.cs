using System;
using System.Collections.Generic;
using System.Reactive.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Messages
{
    public class MessageService : IMessageService
    {
        IMessageStorage storage = new MessageStorage();

        private static event EventHandler MessagesChanged;

        public IObservable<List<MessageThreadItem>> GetMessageThreads()
        {
            return Observable.FromAsync(GetThreadsAsync);
        }

        async Task<List<MessageThreadItem>> GetThreadsAsync()
        {
            await new MessageFetcher().FetchAsync();
            return await storage.GetThreadsWithAdditionInformationAsync();
        }

        public IObservable<List<PrivateMessage>> GetMessages(string username)
        {
            return Observable
                .FromAsync(() => storage.GetMessagesAsync(username))
                .Merge(Observable
                    .FromEventPattern(typeof(MessageService), "MessagesChanged")
                    .SelectMany(Observable.FromAsync(() => storage.GetMessagesAsync(username))));
        }

        public async Task SendMessage(string username, string message)
        {
            await new ReactorMessageParser().SendMessageToUser(username, message);
            await storage.PutMessageAsync(
                username,
                new PrivateMessage
                {
                    Message = message,
                    Created = DateTime.Now,
                    Mode = PrivateMessage.ModeOutbox
                });
            MessagesChanged?.Invoke(null, null);
        }

        public interface IMessageStorage
        {
            Task<List<MessageThreadItem>> GetThreadsWithAdditionInformationAsync();

            Task<List<PrivateMessage>> GetMessagesAsync(string username);

            Task PutMessageAsync(string username, PrivateMessage message);
        }
    }
}