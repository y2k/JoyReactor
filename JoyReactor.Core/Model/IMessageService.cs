using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model
{
	public interface IMessageService
	{
		IObservable<List<MessageThreadItem>> GetMessageThreads ();

		IObservable<List<PrivateMessage>> GetMessages (string username);

        Task SendMessage(string username, string message);
	}

	public class MessageThreadItem
	{
		public string UserName { get; set; }

		public string UserImage { get; set; }

		public string LastMessage { get; set; }
	}
}