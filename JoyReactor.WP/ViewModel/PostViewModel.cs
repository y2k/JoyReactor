using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using JoyReactor.WP.Common;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.ViewModel
{
    public class PostViewModel : ViewModelBase
    {
        public ObservableCollection<ItemPostViewModel> Posts { get; private set; }

        private int _currentPosition;
        public int CurrentPosition { get { return _currentPosition; } set { Set(ref _currentPosition, value); } }

        private IPostCollectionModel posModel = InjectService.Locator.GetInstance<IPostCollectionModel>();

        public PostViewModel()
        {
            Posts = new ObservableCollection<ItemPostViewModel>();
            //
        }

        public async void Initialize(ID listId, string postId)
        {
            Messenger.Default.Send(new NavigationMessage { ViewModel = this });
        }

        public class ItemPostViewModel : ViewModelBase
        {

        }
    }
}