using System;
using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;

namespace JoyReactor.Core.ViewModels
{
	public class MessagesViewModel : ViewModelBase
	{
		public ObservableCollection<Item> Messages { get; } = new ObservableCollection<Item>();

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
				Messages.Add (new Item { 
					Created = DateTime.Now + TimeSpan.FromHours (rand.Next (-1000, 1000)),
					Message = "Test message " + rand.NextDouble ()
				});
			#endif
		}

		public class Item : ViewModelBase
		{
			public string Message { get; set; }

			public DateTime Created { get; set; }

			public bool IsMine { get; set; }
		}

		public class SelectThreadMessage
		{
			public string Username { get; set; }
		}
	}
}