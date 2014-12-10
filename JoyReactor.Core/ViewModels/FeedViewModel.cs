using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
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

        private List<ViewModelBase> _posts;
        public List<ViewModelBase> Posts
        {
            get { return _posts; }
            set { Set(ref _posts, value); }
        }

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

        public FeedViewModel() : this(ID.Factory.New(ID.IdConst.ReactorGood)) { }

        public FeedViewModel(ID id)
        {
            if (!IsInDesignMode)
            {
                RefreshCommand = new RelayCommand(OnRefreshInvoked);
                MoreCommand = new RelayCommand(OnButtonMoreClicked);
                ApplyCommand = new RelayCommand(OnApplyButtonClicked);
                ChangeCurrentListIdCommand = new RelayCommand<ID>(OnChangeCurrentListId);
                LoadFirstPage(id);
            }
        }

        async void LoadFirstPage(ID newId)
        {
            id = newId;

            IsBusy = true;
            await ReloadDataFromDatabase();
            await model.SyncFirstPage(id);
            await ReloadDataFromDatabase();
            IsBusy = false;
        }

        async Task ReloadDataFromDatabase()
        {
            var data = await model.Get(id);
            HasNewItems = data.NewItemsCount > 0;
            Posts = ConvertToViewModelItemList(data);
        }

        private List<ViewModelBase> ConvertToViewModelItemList(PostCollectionState data)
        {
            var posts = data.Posts.Select(s => new ContentViewModel(s)).ToList<ViewModelBase>();
            if (data.DividerPosition >= 0)
                posts.Insert(data.DividerPosition, new DividerViewModel());
            return posts;
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
            await ReloadDataFromDatabase();
            IsBusy = false;
        }

        async void OnApplyButtonClicked()
        {
            await model.ApplyNewItems(id);
            await ReloadDataFromDatabase();
        }

        async void OnButtonMoreClicked()
        {
            await model.SyncNextPage(id);
            await ReloadDataFromDatabase();
        }

        void OnChangeCurrentListId(ID newId)
        {
            LoadFirstPage(newId);
        }

        public class ContentViewModel : ViewModelBase
        {
            public string Title { get { return post.Title; } }
            public string Image { get { return post.Image; } }

            private Post post;

            public ContentViewModel(Post post)
            {
                this.post = post;
            }
        }

        public class DividerViewModel : ViewModelBase
        {
            // TODO:
        }
    }
}