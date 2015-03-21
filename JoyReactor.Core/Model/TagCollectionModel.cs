using System;
using System.Collections.Generic;
using System.Reactive.Concurrency;
using System.Reactive.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System.Reactive;

namespace JoyReactor.Core.Model
{
    public class TagCollectionModel
    {
        internal static event EventHandler InvalidateEvent;

        public static void OnInvalidateEvent()
        {
            InvalidateEvent?.Invoke(null, null);
        }

        [Obsolete]
        SQLiteConnection connection = ServiceLocator.Current.GetInstance<SQLiteConnection>();
        Storage storage = ServiceLocator.Current.GetInstance<Storage>();

        [Obsolete]
        public Task<List<Tag>> GetMainSubscriptionsAsync()
        {
            return DoGetSubscriptionsAsync();
        }

        public IObservable<List<Tag>> GetMainSubscriptions()
        {
            var first = Observable.FromAsync(DoGetSubscriptionsAsync);
            var second = Observable
                .FromEventPattern(typeof(TagCollectionModel), "InvalidateEvent")
                .SelectMany(_ => Observable.FromAsync(DoGetSubscriptionsAsync));
            return first.Concat(second);
        }

        Task<List<Tag>> DoGetSubscriptionsAsync()
        {
            return connection.QueryAsync<Tag>("SELECT * FROM tags WHERE Flags & ? != 0", Tag.FlagShowInMain);
        }

        public IObservable<ICollection<TagGroup>> GetLinkedTags(ID tagId) {
            return Observable
                .FromEventPattern(e => InvalidateEvent += e, e => InvalidateEvent -= e)
                .StartWith((EventPattern<object>)null)
                .SelectMany(_ => Observable.FromAsync(() => storage.GetLinkedTagsAsync(tagId)));
        }

        public interface Storage {

            Task<ICollection<TagGroup>> GetLinkedTagsAsync(ID id);
        }
    }
}