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
        public int CurrentPosition { get { return _currentPosition; } set { Set(ref _currentPosition, value); OnPositionChanged(); } }

        private IPostCollectionModel model = InjectService.Locator.GetInstance<IPostCollectionModel>();
        private ID listId;

        public PostViewModel()
        {
            Posts = new ObservableCollection<ItemPostViewModel>();
        }

        public async void Initialize(ID listId, string postId)
        {
            this.listId = listId;

            Posts.Clear();
            for (int i = 0; i < await model.GetCountAsync(listId); i++) Posts.Add(new ItemPostViewModel(listId, i));
            OnPositionChanged();

            Messenger.Default.Send(new NavigationMessage { ViewModel = this });
        }

        private void OnPositionChanged()
        {
            for (int i = Math.Max(0, CurrentPosition - 1); i <= Math.Min(Posts.Count - 1, CurrentPosition + 1); i++)
            {
                Posts[i].Initialize();
            }
        }

        public class ItemPostViewModel : ViewModelBase
        {
            private string _title;
            public string Title { get { return _title; } set { Set(ref _title, value); } }

            private Uri _image;
            public Uri Image { get { return _image; } set { Set(ref _image, value); } }

            public ObservableCollection<string> Comments { get; private set; }

            private IPostModel model = InjectService.Locator.GetInstance<IPostModel>();

            private ID listId;
            private int position;

            public ItemPostViewModel(ID listId, int position)
            {
                Comments = new ObservableCollection<string>();
                this.listId = listId;
                this.position = position;
            }

            internal async void Initialize()
            {
                Comments.Clear();

                var post = await model.GetPostAsync(listId, position);
                Title = post.Title;
                Image = post.Image == null ? null : new Uri(post.Image);

                (await model.GetCommentsAsync(post.Id, 0)).ForEach(s => Comments.Add(s));
            }
        }
    }
}