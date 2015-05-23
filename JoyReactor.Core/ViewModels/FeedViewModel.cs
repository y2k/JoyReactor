using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Core.ViewModels
{
    public class FeedViewModel : ViewModelBase
    {
        #region Commands

        public RelayCommand RefreshCommand { get; set; }

        public RelayCommand MoreCommand { get; set; }

        public RelayCommand ApplyCommand { get; set; }

        public RelayCommand<ID> ChangeCurrentListIdCommand { get; set; }

        #endregion

        #region Properties

        public ObservableCollection<ViewModelBase> Posts { get; } = new ObservableCollection<ViewModelBase>();

        bool _isBusy;

        public bool IsBusy { get { return _isBusy; } set { Set(ref _isBusy, value); } }

        bool _hasNewItems;

        public bool HasNewItems { get { return _hasNewItems; } set { Set(ref _hasNewItems, value); } }

        ErrorType _error;

        public ErrorType Error { get { return _error; } set { Set(ref _error, value); } }

        #endregion

        IFeedService service;
        IDisposable subscription;

        public FeedViewModel()
        {
            RefreshCommand = new RelayCommand(ReloadFeed);
            MoreCommand = new RelayCommand(LoadNextPage);
            ApplyCommand = new RelayCommand(async () => await service.ApplyNewItemsAsync());
            ChangeCurrentListIdCommand = new RelayCommand<ID>(Initialize);

            MessengerInstance.Register<TagsViewModel.SelectTagMessage>(this, s => ChangeCurrentListIdCommand.Execute(s.Id));
        }

        public void Initialize(ID newId)
        {
            service = new FeedService(newId);
            ClearState();

            subscription?.Dispose();
            subscription = service
                .Get()
                .SubscribeOnUi(data =>
                {
                    Error = ErrorType.NotError;
                    HasNewItems = data.NewItemsCount > 0;
                    IsBusy = false;
                    UpdatePosts(data);
                }, error =>
                {
                    ClearState();
                    Error = ErrorType.NotAuthorized;
                });
        }

        void ClearState()
        {
            Error = ErrorType.NotError;
            HasNewItems = IsBusy = false;
            Posts.Clear();
        }

        void UpdatePosts(PostCollectionState data)
        {
            var newPosts = ConvertToViewModelItemList(true, data);
            for (int i = Posts.Count - 1; i >= newPosts.Count; i--)
                Posts.RemoveAt(i);
            for (int i = Posts.Count; i < newPosts.Count; i++)
                Posts.Add(null);
            for (int i = 0; i < newPosts.Count; i++)
                Posts[i] = newPosts[i];
        }

        async void ReloadFeed()
        {
            IsBusy = true;
            if (HasNewItems)
                await service.ApplyNewItemsAsync();
            else
                await service.ResetAsync();
            IsBusy = false;
        }

        async void LoadNextPage()
        {
            IsBusy = true;
            await service.SyncNextPageAsync();
            IsBusy = false;
        }

        List<ViewModelBase> ConvertToViewModelItemList(bool showDivider, PostCollectionState data)
        {
            var posts = data.Posts.Select(s => new ContentViewModel(s)).ToList<ViewModelBase>();
            if (posts.Count > 0 && data.DividerPosition >= 0)
            {
                var divider = showDivider
                    ? new DividerViewModel(LoadNextPage)
                    : new DividerViewModel(() => { });
                posts.Insert(data.DividerPosition, divider);
            }
            return posts;
        }

        public class ContentViewModel : ViewModelBase
        {
            const string ImageStub = "http://wiki.solid-run.com/images/7/75/No_image_available.png";

            public RelayCommand OpenPostCommand { get; set; }

            public string Title { get { return post.Title; } }

            public string Image { get { return post.Image ?? ImageStub; } }

            public int ImageWidth { get { return Math.Max(1, post.ImageWidth); } }

            public int ImageHeight { get { return Math.Max(1, post.ImageHeight); } }

            public Uri UserImage { get { return post.UserImage == null ? null : new Uri(post.UserImage); } }

            public string UserName { get { return post.UserName; } }

            public DateTime Created { get { return post.Created.DateTimeFromUnixTimestampMs(); } }

            Post post;

            public ContentViewModel(Post post)
            {
                this.post = post;
                OpenPostCommand = new Command(() =>
                    MessengerInstance.Send(new PostNavigationMessage { PostId = post.Id }));
            }

            public override bool Equals(object obj)
            {
                var o = obj as ContentViewModel;
                return o != null && post.PostId == o.post.PostId;
            }

            public override int GetHashCode()
            {
                return base.GetHashCode();
            }
        }

        public class DividerViewModel : ViewModelBase
        {
            public RelayCommand LoadMoreCommand { get; set; }

            public DividerViewModel(Action command)
            {
                LoadMoreCommand = new RelayCommand(command);
            }

            public override bool Equals(object obj)
            {
                return obj is DividerViewModel;
            }

            public override int GetHashCode()
            {
                return base.GetHashCode();
            }
        }

        internal interface IFeedService
        {
            Task ApplyNewItemsAsync();

            IObservable<PostCollectionState> Get();

            Task SyncNextPageAsync();

            Task ResetAsync();
        }

        public enum ErrorType
        {
            NotError,
            NotAuthorized,
        }
    }
}