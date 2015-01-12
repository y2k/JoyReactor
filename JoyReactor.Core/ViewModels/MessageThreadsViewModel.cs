using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Reactive.Linq;
using System.Threading;
using GalaSoft.MvvmLight;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.ViewModels
{
	public class MessageThreadsViewModel : ViewModelBase
	{
		public ObservableCollection<MessageThreadItem> Threads { get; } = new ObservableCollection<MessageThreadItem>();

		bool _isBusy;

		public bool IsBusy
		{
			get { return _isBusy; }
			set { Set(ref _isBusy, value); }
		}

		int _selectedIndex = -1;

		public int SelectedIndex
		{
			get { return _selectedIndex; }
			set { Set(ref _selectedIndex, value); }
		}

		IDisposable subscription;

		public void Initialize()
		{
			IsBusy = true;
			subscription = ServiceLocator.Current
                .GetInstance<IMessageService>()
                .GetMessageThreads()
                .ObserveOn(SynchronizationContext.Current)
                .Subscribe(onNext: OnNext, onError: OnError);

			PropertyChanged += MessageThreadsViewModel_PropertyChanged;
		}

		void MessageThreadsViewModel_PropertyChanged(object sender, PropertyChangedEventArgs e)
		{
			if (e.PropertyName == GetPropertyName(() => SelectedIndex) && SelectedIndex >= 0)
				MessengerInstance.Send(new MessagesViewModel.SelectThreadMessage { Username = Threads[SelectedIndex].UserName });
		}

		void OnNext(List<MessageThreadItem> threads)
		{
			IsBusy = false;
			Threads.ReplaceAll(threads);
		}

		void OnError(Exception e)
		{
			// TODO
			IsBusy = false;
		}

		public override void Cleanup()
		{
			base.Cleanup();
			subscription?.Dispose();
			PropertyChanged -= MessageThreadsViewModel_PropertyChanged;
		}
	}
}