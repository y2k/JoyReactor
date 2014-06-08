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

        private IPostCollectionModel pModel = InjectService.Locator.GetInstance<IPostCollectionModel>();

        public PostViewModel()
        {
            Posts = new ObservableCollection<ItemPostViewModel>();
            //

            if (IsInDesignMode)
            {
                Posts.Add(new ItemPostViewModel { Title = "Post 1" });
                Posts.Add(new ItemPostViewModel { Title = "Post 2" });
                Posts.Add(new ItemPostViewModel { Title = "Post 3" });
            }
        }

        public async void Initialize(ID listId, string postId)
        {
            Messenger.Default.Send(new NavigationMessage { ViewModel = this });

            CurrentPosition = -1;
            Posts.Clear();

            var l = await pModel.GetPostsAsync(listId);
            l.ForEach(s => {
                Posts.Add(new ItemPostViewModel { Title = s.Title ?? "<EMPTY>", Image = s.Image == null ? null : new Uri(s.Image) });
                if (s.PostId == postId) CurrentPosition = Posts.Count - 1;
            });
        }

        public class ItemPostViewModel : ViewModelBase
        {
            private string _title;
            public string Title { get { return _title; } set { Set(ref _title, value); } }

            private Uri _image;
            public Uri Image { get { return _image; } set { Set(ref _image, value); } }
        }
    }
}