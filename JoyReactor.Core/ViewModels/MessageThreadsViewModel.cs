using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reactive.Linq;
using System.Threading;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.ViewModels
{
	public class MessageThreadsViewModel : ViewModelBase
	{
		public ObservableCollection<ItemViewModel> Threads { get; } = new ObservableCollection<ItemViewModel> ();

		bool _isBusy;

		public bool IsBusy {
			get { return _isBusy; }
			set { Set (ref _isBusy, value); }
		}

		IDisposable subscription;

		public void Initialize ()
		{
			IsBusy = true;
			subscription = ServiceLocator.Current
				.GetInstance<IMessageService> ()
				.GetThreads ()
				.ObserveOn (SynchronizationContext.Current)
				.Subscribe (onNext: OnNext, onError: OnError);
		}

		void OnNext (List<MessageThreadItem> threads)
		{
			IsBusy = false;
			Threads.ReplaceAll (from s in threads
			                    select new ItemViewModel (s));
		}

		void OnError (Exception e)
		{
			// TODO
			IsBusy = false;
		}

		public override void Cleanup ()
		{
			base.Cleanup ();
			subscription.Dispose ();
		}

		public class ItemViewModel : ViewModelBase
		{
			public string Username { get; set; }

			public string LastMessage { get; set; }

			public RelayCommand OpenThreadCommand { get; set; }

			public ItemViewModel (MessageThreadItem item)
			{
				Username = item.UserName;
				LastMessage = item.LastMessage;

				OpenThreadCommand = new FixRelayCommand (() =>
					MessengerInstance.Send (new MessagesViewModel.SelectThreadMessage { Username = Username }));
			}
		}
	}
}