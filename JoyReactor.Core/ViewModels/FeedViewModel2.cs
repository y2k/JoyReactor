using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.ViewModels
{
    public class FeedViewModel2 : ScopedViewModel
    {
        public FeedViewModel.ErrorType Error { get { return FeedViewModel.ErrorType.NotError; } }

        public ObservableCollection<Post> Posts { get; } = new ObservableCollection<Post>();

        public bool IsBusy { get { return Get<bool>(); } set { Set(value); } }

        public bool HasNewItems { get { return Get<bool>(); } set { Set(value); } }

        public ICommand ApplyCommand { get; private set; }

        public ICommand SelectItemCommand { get; private set; }

        public ICommand RefreshCommand { get; set; }

        PostCollectionRequest firstPageRequest;
        ID id;
        int nextPage;

        public FeedViewModel2()
        {
            ApplyCommand = new Command(ApplyCommandMethod);
            SelectItemCommand = new Command<int>(SelectItemCommandMethod);
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

            IsBusy = true;
            var tag = await new TagRepository().GetAsync(id.SerializeToString());
            Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));

            firstPageRequest = new PostCollectionRequest(id, 0);
            await firstPageRequest.DownloadFromWebAsync();
            nextPage = firstPageRequest.NextPage;

            await new PostRepository().UpdateOrInsertAllAsync(firstPageRequest.Posts);

            if (Posts.Count == 0 || IsStartWith(Posts, firstPageRequest.Posts))
                await ApplyCommandMethod();
            else
                HasNewItems = true;

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
            {
                await LoadNextPage();
            }
            else
            {
                // TODO:
            }
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

        public class Divider : Post
        {
        }
    }
}