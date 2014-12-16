using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

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

        public bool IsBusy
        {
            get { return _isBusy; }
            set { Set(ref _isBusy, value); }
        }

        bool _hasNewItems;

        public bool HasNewItems
        {
            get { return _hasNewItems; }
            set { Set(ref _hasNewItems, value); }
        }

        #endregion

        PostCollectionModel model = new PostCollectionModel();
        ID id;

        public FeedViewModel() : this(ID.Factory.New(ID.IdConst.ReactorGood))
        {
        }

        public FeedViewModel(ID id)
        {
            RefreshCommand = new RelayCommand(OnRefreshInvoked);
            MoreCommand = new RelayCommand(OnButtonMoreClicked);
            ApplyCommand = new RelayCommand(OnApplyButtonClicked);
            ChangeCurrentListIdCommand = new RelayCommand<ID>(LoadFirstPage);

            MessengerInstance.Register<SelectTagMessage>(this, s => ChangeCurrentListIdCommand.Execute(s.Id));
        }

        async void LoadFirstPage(ID newId)
        {
            id = newId;

            IsBusy = true;
            await ReloadDataFromDatabase(false);
            await model.SyncFirstPage(id);
            await ReloadDataFromDatabase(true);
            IsBusy = false;
        }

        async void OnRefreshInvoked()
        {
            IsBusy = true;
            if (HasNewItems)
            {
                await model.ApplyNewItems(id);
            }
            else
            {
                await model.Reset(id);
                await model.SyncFirstPage(id);
            }
            await ReloadDataFromDatabase(true);
            IsBusy = false;
        }

        async void OnApplyButtonClicked()
        {
            await model.ApplyNewItems(id);
            await ReloadDataFromDatabase(true);
        }

        async void OnButtonMoreClicked()
        {
            IsBusy = true;
            await model.SyncNextPage(id);
            await ReloadDataFromDatabase(true);
            IsBusy = false;
        }

        async Task ReloadDataFromDatabase(bool showDivider)
        {
            var data = await model.Get(id);
            HasNewItems = data.NewItemsCount > 0;

            var newPosts = ConvertToViewModelItemList(showDivider, data);
            for (int i = Posts.Count - 1; i >= newPosts.Count; i--)
                Posts.RemoveAt(i);
            for (int i = Posts.Count; i < newPosts.Count; i++)
                Posts.Add(null);
            for (int i = 0; i < newPosts.Count; i++)
                Posts[i] = newPosts[i];
        }

        List<ViewModelBase> ConvertToViewModelItemList(bool showDivider, PostCollectionState data)
        {
            var posts = data.Posts.Select(s => new ContentViewModel(s)).ToList<ViewModelBase>();
            if (data.DividerPosition >= 0)
            {
                var divider = showDivider
                    ? new DividerViewModel(OnButtonMoreClicked)
                    : new DividerViewModel(() => { });
                posts.Insert(data.DividerPosition, divider);
            }
            return posts;
        }

        public class ContentViewModel : ViewModelBase
        {
            public RelayCommand OpenPostCommand { get; set; }

            public string Title { get { return post.Title; } }

            public string Image { get { return post.Image; } }

            public int ImageWidth { get { return post.ImageWidth; } }

            public int ImageHeight { get { return post.ImageHeight; } }

            public Uri UserImage { get { return post.UserImage == null ? null : new Uri(post.UserImage); } }

            public string UserName { get { return post.UserName; } }

            Post post;

            public ContentViewModel(Post post)
            {
                this.post = post;
                OpenPostCommand = new FixRelayCommand(() =>
                   MessengerInstance.Send(new PostNavigationMessage { PostId = post.Id }));
            }

            public override bool Equals(object obj)
            {
                var o = obj as ContentViewModel;
                return o != null && post.PostId == o.post.PostId;
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
        }
    }
}