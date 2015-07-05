using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels.Common;

namespace JoyReactor.Core.ViewModels
{
    public class FeedViewModel : ScopedViewModel
    {
        public ErrorType Error { get { return Get<ErrorType>(); } set { Set(value); } }

        public ObservableCollection<Post> Posts { get; } = new ObservableCollection<Post>();

        public bool IsBusy { get { return Get<bool>(); } set { Set(value); } }

        public bool HasNewItems { get { return Get<bool>(); } set { Set(value); } }

        public ICommand ApplyCommand { get; private set; }

        public ICommand SelectItemCommand { get; private set; }

        public ICommand OpenImageCommand { get; private set; }

        public ICommand RefreshCommand { get; set; }

        PostCollectionRequest firstPageRequest;
        ID id;
        int nextPage;

        public FeedViewModel()
        {
            Error = ErrorType.NotError;
            ApplyCommand = new Command(ApplyCommandMethod);
            SelectItemCommand = new Command<int>(SelectItemCommandMethod);
            RefreshCommand = new Command(RefreshCommandMethod);

            OpenImageCommand = new Command<int>(index =>
            {
                var post = Posts[index];
                OpenImageInFullscreen(post.Video ?? post.Image);
            });

            SetCurrentTag(ID.ReactorGood);
        }

        public override void OnActivated()
        {
            base.OnActivated();
            MessengerInstance.Register<TagsViewModel.SelectTagMessage>(this, m => SetCurrentTag(m.Id));
        }

        async void SetCurrentTag(ID id)
        {
            this.id = id;

            HasNewItems = false;
            IsBusy = true;
            Error = ErrorType.NotError;

            try
            {
                var tag = await new TagRepository().GetAsync(id.SerializeToString());
                Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));

                firstPageRequest = new PostCollectionRequest(id, 0);
                await firstPageRequest.DownloadFromWebAsync();
                nextPage = firstPageRequest.NextPage;

                await new TagCollectionModel().UpdateTagAsync(id, firstPageRequest);
                await new PostRepository().UpdateOrInsertAllAsync(firstPageRequest.Posts);

                if (Posts.Count == 0 || IsStartWith(Posts, firstPageRequest.Posts))
                    await ApplyCommandMethod();
                else
                    HasNewItems = true;
            }
            catch (Exception e)
            {
                Error = ErrorType.UnkownError;
            }
            IsBusy = false;
        }

        bool IsStartWith(IList<Post> originalPosts, IList<Post> newPosts)
        {
            if (originalPosts.Count < newPosts.Count)
                return false;
            for (int i = 0; i < newPosts.Count; i++)
                if (originalPosts[i].PostId != newPosts[i].PostId)
                    return false;
            return true;
        }

        public async Task SelectItemCommandMethod(int index)
        {
            var item = Posts[index];
            if (item is Divider)
                await LoadNextPage();
            else
                MessengerInstance.Send(new PostNavigationMessage { PostId = item.Id });
        }

        void OpenImageInFullscreen(string mediaUri)
        {
            //var post = Posts[index];
            //var mediaUri = post.Video ?? post.Image;
            if (GalleryViewModel.IsCanShow(mediaUri))
                BaseNavigationService.Instance.ImageFullscreen(mediaUri);
        }

        async Task LoadNextPage()
        {
            IsBusy = true;

            var tag = await new TagRepository().GetAsync(id.SerializeToString());
            var nextPageRequest = new PostCollectionRequest(id, nextPage);
            await nextPageRequest.DownloadFromWebAsync();
            nextPage = nextPageRequest.NextPage;

            await new PostRepository().UpdateOrInsertAllAsync(nextPageRequest.Posts);

            var ids = new List<TagPost>();
            foreach (var s in GetBeforeDivider())
                ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
            foreach (var s in nextPageRequest.Posts)
                if (ids.All(i => i.PostId != s.Id))
                    ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
            int dividerPosition = ids.Count;
            foreach (var s in GetAfterDivider())
                if (ids.All(i => i.PostId != s.Id))
                    ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
            await new TagPostRepository().ReplaceAllForTagAsync(ids);

            Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));
            Posts.Insert(dividerPosition, new Divider());

            IsBusy = false;
        }

        public async Task ApplyCommandMethod()
        {
            var tag = await new TagRepository().GetAsync(id.SerializeToString());
            var ids = new List<TagPost>();
            foreach (var s in firstPageRequest.Posts)
                ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
            foreach (var s in GetBeforeDivider())
                if (ids.All(i => i.PostId != s.Id))
                    ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
            await new TagPostRepository().ReplaceAllForTagAsync(ids);
            Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));
            Posts.Insert(firstPageRequest.Posts.Count, new Divider());

            HasNewItems = false;
        }

        IEnumerable<Post> GetBeforeDivider()
        {
            return Posts.TakeWhile(s => !(s is Divider));
        }

        IEnumerable<Post> GetAfterDivider()
        {
            return Posts.SkipWhile(s => !(s is Divider)).Skip(1);
        }

        public async Task RefreshCommandMethod()
        {
            if (IsBusy)
                return;

            if (HasNewItems)
            {
                IsBusy = true;
                await ApplyCommandMethod();
                IsBusy = false;
            }
            else
            {
                IsBusy = true;
                Posts.Clear();

                var tag = await new TagRepository().GetAsync(id.SerializeToString());
                await new TagPostRepository().RemoveAllAsync(tag.Id);
                SetCurrentTag(id);
            }
        }

        public class Divider : Post
        {
        }

        public enum ErrorType
        {
            NotError,
            UnkownError,
        }
    }
}