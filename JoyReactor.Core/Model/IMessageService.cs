using System;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model
{
	public interface IMessageService
	{
		IObservable<List<MessageThreadItem>> GetThreads ();

		IObservable<List<PrivateMessage>> GetMessages (string username);
	}

	public class MessageThreadItem
	{
		public string Username { get; set; }

		public string LastMessage { get; set; }
	}
}