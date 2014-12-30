using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Reactive.Concurrency;
using System.Reactive.Disposables;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class TagCollectionModel
    {
        SQLiteConnection connection = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task<List<Tag>> GetMainSubscriptionsAsync()
        {
            return Task.Run(() =>
                connection.SafeQuery<Tag>("SELECT * FROM tags WHERE Flags & ? != 0", Tag.FlagShowInMain));
        }

        public IObservable<List<Tag>> GetMainSubscriptions()
        {
            //return Observable.Create<List<Tag>>(observer =>
            //{
            //    Scheduler.CurrentThread.Schedule(async () =>
            //    {
            //        var data = await GetMainSubscriptionsAsync();
            //        observer.OnNext(data);
            //    });
            //});
            return Observable.Create<List<Tag>>(observer =>
            {
                var cancel = new CancellationDisposable();
                Scheduler.CurrentThread.Schedule(async () =>
                {
                    while (!cancel.Token.IsCancellationRequested)
                    {
                        var data = await GetMainSubscriptionsAsync();
                        observer.OnNext(data);
                        await Task.Delay(2000);
                    }
                });
                return cancel;
            });
        }

        public Task<List<TagLinkedTag>> GetTagLinkedTagsAsync(ID tagId)
        {
            return Task.Run(() =>
            {
                return connection.SafeQuery<TagLinkedTag>(
                    "SELECT * FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)",
                    MainDb.ToFlatId(tagId));
            });
        }
    }
}