using GalaSoft.MvvmLight;
using System.Reactive.Concurrency;
using System.Threading;
using System;

namespace JoyReactor.Core.ViewModels
{
    public class ViewModel : ViewModelBase
    {
        readonly Lazy<SynchronizationContextScheduler> _uiScheduler = 
            new Lazy<SynchronizationContextScheduler>(() => new SynchronizationContextScheduler(SynchronizationContext.Current));

        public IScheduler UiScheduler { get { return _uiScheduler.Value; } }
    }
}