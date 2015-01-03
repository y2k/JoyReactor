using System;
using System.Linq;
using System.Reactive.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Helper
{
    static class ObservableFactory
    {
        public static IObservable<long> Interval(TimeSpan period)
        {
            return Observable.Return(0L).Concat(Observable.Interval(period));
        }
        public static IObservable<T> IntervalAsync<T>(TimeSpan period, Func<Task<T>> func)
        {
            return Interval(period)
                .SelectMany(_ => Observable.FromAsync(func));
        }
    }
}