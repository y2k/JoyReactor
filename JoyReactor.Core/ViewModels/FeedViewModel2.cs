using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace JoyReactor.Core.ViewModels
{
    public class FeedViewModel2 : ViewModel
    {
        public ObservableCollection<Post> Posts { get; } = new ObservableCollection<Post>();

        public bool IsBusy { get { return Get<bool>(); } set { Set(value); } }

        public bool HasNewItems { get { return Get<bool>(); } set { Set(value); } }

        public Command

        public FeedViewModel2()
        {
            Init();
        }

        async void Init()
        {
            IsBusy = true;

            var tag = await new TagRepository().GetAsync(ID.Reactor.SerializeToString());
            Posts.ReplaceAll(await new PostRepository().GetAllAsync(tag.Id));

            var provider = new PostCollectionProvider(ID.DeserializeFromString(tag.TagId), 0);
            await provider.DownloadFromWebAsync();

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