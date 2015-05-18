using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Reactive;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class TagCollectionModel
    {
        internal static event EventHandler InvalidateEvent;

        [Obsolete]
        public static void InvalidateTagCollection()
        {
            InvalidateEvent?.Invoke(null, null);
        }

        public static Task InvalidateTagCollectionAsync() {
            return Task.Run(() => InvalidateEvent?.Invoke(null, null));
        }

        [Obsolete]
        AsyncSQLiteConnection connection = ServiceLocator.Current.GetInstance<AsyncSQLiteConnection>();
        Storage storage = ServiceLocator.Current.GetInstance<Storage>();

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