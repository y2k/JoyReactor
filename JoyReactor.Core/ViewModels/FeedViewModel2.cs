using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows.Input;
using System.Linq;

namespace JoyReactor.Core.ViewModels
{
    public class FeedViewModel2 : ViewModel
    {
        public ObservableCollection<Post> Posts { get; } = new ObservableCollection<Post>();

        public bool IsBusy { get { return Get<bool>(); } set { Set(value); } }

        public bool HasNewItems { get { return Get<bool>(); } set { Set(value); } }

        public ICommand ApplyCommand { get; private set; }

        PostCollectionRequest provider;

        public FeedViewModel2()
        {
            Init();

            ApplyCommand = new Command(
                async () =>
                {
                    var tag = await new TagRepository().GetAsync(ID.Reactor.SerializeToString());
                    var ids = new List<TagPost>();
                    foreach (var s in provider.Posts)
                        ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
                    foreach (var s in Posts)
                        if (ids.All(i => i.PostId != s.Id))
                            ids.Add(new TagPost { TagId = tag.Id, PostId = s.Id });
                    await new TagPostRepository().ReplaceAllForTagAsync(ids);

                    Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));
                    HasNewItems = false;
                });
        }

        async void Init()
        {
            IsBusy = true;
            var tag = await new TagRepository().GetAsync(ID.Reactor.SerializeToString());
            Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));

            provider = new PostCollectionRequest(ID.DeserializeFromString(tag.TagId), 0);
            await provider.DownloadFromWebAsync();

            await new PostRepository().UpdateOrInsertAllAsync(provider.Posts);

            HasNewItems = !IsContains(Posts, provider.Posts);
            IsBusy = false;
        }

        bool IsContains(IList<Post> posts1, IList<Post> posts2)
        {
            if (posts1.Count <= posts2.Count)
                return false;
            for (int i = 0; i < posts2.Count; i++)
                if (posts1[i].PostId != posts2[i].PostId)
                    return false;
            return true;
        }
    }
}