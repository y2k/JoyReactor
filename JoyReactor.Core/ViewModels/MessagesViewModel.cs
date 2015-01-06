using System;
using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;

namespace JoyReactor.Core.ViewModels
{
	public class MessagesViewModel : ViewModelBase
	{
		public ObservableCollection<MessageItem> Messages { get; } = new ObservableCollection<MessageItem>();

		bool _isBusy;

		public bool IsBusy {
			get { return _isBusy; }
			set { Set (ref _isBusy, value); }
		}

		public MessagesViewModel ()
		{
			MessengerInstance.Register<SelectThreadMessage> (this, m => SwitchUser (m.Username));
		}

		void SwitchUser (string username)
		{
			// TODO
			#if DEBUG
			var rand = new Random ();
			Messages.Clear ();
			for (int i = rand.Next (30); i >= 0; i--)
				Messages.Add (new MessageItem { Message = "Test message " + rand.NextDouble () });
			#endif
		}

		public class MessageItem
		{
			public string Message { get; set; }

			public DateTime Created { get; set; }
		}

		public class SelectThreadMessage
		{
			public string Username { get; set; }
		}
	}
}