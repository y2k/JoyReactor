using GalaSoft.MvvmLight;
using System.Reactive.Concurrency;
using System.Threading;

namespace JoyReactor.Core.ViewModels
{
    public class ViewModel : ViewModelBase
    {
        public IScheduler UiScheduler { get; set; } = new SynchronizationContextScheduler(SynchronizationContext.Current);
    }
}