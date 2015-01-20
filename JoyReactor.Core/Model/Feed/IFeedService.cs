using System;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Feed
{
    interface IFeedService
    {
        IObservable<PostCollectionState> Get(ID id);

        Task ApplyNewItemsAsync(ID id);

        Task ResetAsync(ID id);

        Task LoadNextPage(ID id);
    }
}