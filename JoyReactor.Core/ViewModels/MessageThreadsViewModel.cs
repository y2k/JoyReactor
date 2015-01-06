using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;

namespace JoyReactor.Core.ViewModels
{
	public class MessageThreadsViewModel : ViewModelBase
	{
		public ObservableCollection<MessageThreadItem> Threads { get; } = new ObservableCollection<MessageThreadItem> ();

		bool _isBusy;

		public bool IsBusy {
			get { return _isBusy; }
			set { Set (ref _isBusy, value); }
		}

		public void Initialize ()
		{
			//
			#if DEBUG
			Threads.Add (new MessageThreadItem { Username = "Songe", LastMessage = "Привет" });
			Threads.Add (new MessageThreadItem { Username = "Felost", LastMessage = "Zettai Ryouiki разное" });
			Threads.Add (new MessageThreadItem { Username = "ikari", LastMessage = "Доставило сочетание XD" });
			Threads.Add (new MessageThreadItem { Username = "janklodvandam", LastMessage = "Фэндом соника на джое." });
			Threads.Add (new MessageThreadItem { Username = "MADba11", LastMessage = "Gone too fast." });
			#endif
		}

		public class MessageThreadItem: ViewModelBase
		{
			public string Username { get; set; }

			public string LastMessage { get; set; }

			public RelayCommand OpenThreadCommand { get; set; }

			public MessageThreadItem ()
			{
				OpenThreadCommand = new FixRelayCommand (() =>
					MessengerInstance.Send (new MessagesViewModel.SelectThreadMessage { Username = Username }));
			}
		}
	}
}