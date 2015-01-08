using System;
using System.Collections.Generic;
using System.Reactive.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Messages
{
	public class MessageService : IMessageService
	{
		public IObservable<List<MessageThreadItem>> GetThreads ()
		{
			return Observable.FromAsync (GetThreadsAsync);
		}

		async Task<List<MessageThreadItem>> GetThreadsAsync ()
		{
			var storage = new MessageStorage ();
			await new MessagerFetcher (storage, new ReactorMessageParser ()).FetchAsync ();
			return await storage.GetThreadsWithAdditionInformationAsync ();
		}

		public IObservable<List<PrivateMessage>> GetMessages (string username)
		{
			throw new NotImplementedException ();
		}
	}
}