using System;
using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Reactive.Linq;
using System.Threading;
using System.Collections.Generic;
using GalaSoft.MvvmLight.Command;

namespace JoyReactor.Core.ViewModels
{
    public class MessagesViewModel : ViewModelBase
    {
        public ObservableCollection<PrivateMessage> Messages { get; } = new ObservableCollection<PrivateMessage>();

        bool _isBusy;
        public bool IsBusy
        {
            get { return _isBusy; }
            set { Set(ref _isBusy, value); }
        }

        string _newMessage;
        public string NewMessage
        {
            get { return _newMessage; }
            set { Set(ref _newMessage, value); }
        }

        public RelayCommand CreateMessageCommand { get; set; }

        IDisposable subscription;
        string currentUserName;

        public MessagesViewModel()
        {
            MessengerInstance.Register<SelectThreadMessage>(this, m => SwitchUser(m.Username));
            CreateMessageCommand = new FixRelayCommand(CreateNewMessage);
        }

        private async void CreateNewMessage()
        {
            IsBusy = true;
            await ServiceLocator.Current.GetInstance<IMessageService>()
                .SendMessage(currentUserName, NewMessage);
            NewMessage = null;
            IsBusy = false;
        }

        void SwitchUser(string username)
        {
            Messages.Clear();
            IsBusy = true;

            subscription?.Dispose();
            subscription = ServiceLocator.Current.GetInstance<IMessageService>()
                .GetMessages(currentUserName = username)
                .ObserveOn(SynchronizationContext.Current)
                .Subscribe(OnNext, OnError);
        }

        void OnNext(List<PrivateMessage> messages)
        {
            // TODO
            Messages.ReplaceAll(messages);
            IsBusy = false;
        }

        void OnError(Exception error)
        {
            // TODO
            IsBusy = false;
        }

        public override void Cleanup()
        {
            base.Cleanup();
            subscription?.Dispose();
        }

        public class SelectThreadMessage
        {
            public string Username { get; set; }
        }
    }
}