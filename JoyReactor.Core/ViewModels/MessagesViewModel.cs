using System;
using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Reactive.Linq;
using System.Threading;
using System.Collections.Generic;

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

        IMessageService service = ServiceLocator.Current.GetInstance<IMessageService>();

        public MessagesViewModel()
        {
            MessengerInstance.Register<SelectThreadMessage>(this, m => SwitchUser(m.Username));
        }

        void SwitchUser(string username)
        {
            Messages.Clear();
            IsBusy = true;

            service
                .GetMessages(username)
                .SubscribeOn(SynchronizationContext.Current)
                .Subscribe(OnNext, OnError);
        }

        void OnNext(List<PrivateMessage> messages)
        {
            // TODO
            IsBusy = false;
            Messages.ReplaceAll(messages);
        }

        void OnError(Exception error)
        {
            // TODO
            IsBusy = false;
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