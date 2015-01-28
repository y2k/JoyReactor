using System;
using System.Reactive.Linq;
using System.Threading;

namespace JoyReactor.Core.Model.Helper
{
    static class ObservableExtensions
    {
        internal static IDisposable SubscribeOnUi<T>(this IObservable<T> source, Action<T> onNext)
        {
            return source
                .ObserveOn(SynchronizationContext.Current)
                .Subscribe(onNext);
        }
    }
}