using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Model
{
    class PostCollectionLoader
    {
        ID id;
        bool isFirstPage;
        PostCollectionProvider provider;

        public PostCollectionLoader(ID id, bool isFirstPage)
        {
            this.isFirstPage = isFirstPage;
            this.id = id;
        }

        public async Task LoadAsync()
        {
            provider = new PostCollectionProvider(id, await GetCurrentPage());
            await provider.DownloadFromWebAsync();

            await UpdatePostContennt();
            await UpdatePostOrderInList();
            await SaveNextPage();
        }

        async Task<int> GetCurrentPage()
        {
            return isFirstPage 
                ? PostCollectionProvider.FirstPage
                : (await new TagRepository().GetAsync(id.SerializeToString())).NextPage;
        }

        async Task SaveNextPage()
        {
            var tag = await new TagRepository().GetAsync(id.SerializeToString());
            tag.NextPage = provider.NextPage;
            await new TagRepository().UpdateAsync(tag);
        }

        async Task UpdatePostContennt()
        {
            var repository = new PostRepository();
            foreach (var post in provider.Posts)
            {
                post.Id = (await repository.GetAsync(post.PostId))?.Id ?? 0;
                if (post.Id == 0)
                    await repository.InsertAsync(post);
                else
                    await repository.UpdateAsync(post);
            }
        }

        Task UpdatePostOrderInList()
        {
            return new TagOrderSaver(id, isFirstPage, provider.Posts.Select(s => s.Id).ToList()).ExecuteAsync();
        }
    }
}