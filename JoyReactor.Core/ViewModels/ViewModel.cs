using System;
using System.Reactive.Concurrency;
using System.Threading;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Views;

namespace JoyReactor.Core.ViewModels
{
    public class ViewModel : ViewModelBase
    {
        readonly Lazy<SynchronizationContextScheduler> _uiScheduler = 
            new Lazy<SynchronizationContextScheduler>(() => new SynchronizationContextScheduler(SynchronizationContext.Current));

        public IScheduler UiScheduler { get { return _uiScheduler.Value; } }

        public static INavigationService NavigationService { get; set; }
    }
}