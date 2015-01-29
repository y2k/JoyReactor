using System.Collections.Generic;
using System.Threading.Tasks;
using System;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Messages
{
	public class MessageFetcher
	{
        IStorage storage = ServiceLocator.Current.GetInstance<IStorage>();
        IMessageParser parser = new ReactorMessageParser();

		public async Task FetchAsync ()
		{
			var messages = await GetAllMyMessagesFromWeb ();
			await storage.ClearAsync ();
			await storage.SaveAsync (messages);
		}

		async Task<List<RawMessage>> GetAllMyMessagesFromWeb ()
		{
			var buffer = new List<RawMessage> ();
			for (int i = 0;; i++) {
				var page = await parser.LoadNextPageAsync (i);
				if (page.Count == 0)
					break;
				buffer.AddRange (page);
			}
			return buffer;
		}

		public interface IStorage
		{
			Task ClearAsync ();

			Task SaveAsync (List<RawMessage> messages);

			Task<List<MessageThreadItem>> GetThreadsWithAdditionInformationAsync ();
		}

		public interface IMessageParser
		{
			Task<List<RawMessage>> LoadNextPageAsync (int page);
		}

		public class RawMessage
		{
			public string Message { get; set; }

			public string UserName { get; set; }

			public string UserImage { get; set; }

			public DateTime Created { get; set; }

			public MessageMode Mode { get; set; }

			public enum MessageMode
			{
				Inbox,
				Outbox
			}
		}
	}
}