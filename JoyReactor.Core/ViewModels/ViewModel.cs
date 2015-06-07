using System;
using System.Reactive.Concurrency;
using System.Threading;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Views;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Linq;

namespace JoyReactor.Core.ViewModels
{
    public class ViewModel : ViewModelBase
    {
        readonly Lazy<SynchronizationContextScheduler> _uiScheduler =
            new Lazy<SynchronizationContextScheduler>(() => new SynchronizationContextScheduler(SynchronizationContext.Current));

        public IScheduler UiScheduler { get { return _uiScheduler.Value; } }

        public static INavigationService NavigationService { get; set; }

        PropertyHolder properties;

        public ViewModel()
        {
            properties = new PropertyHolder(RaisePropertyChanged);
        }

        protected bool Set<T>(T newValue, [CallerMemberName] string propertyName = null)
        {
            return properties.Set<T>(newValue, propertyName);
        }

        protected T Get<T>([CallerMemberName] string propertyName = null)
        {
            return properties.Get<T>(propertyName);
        }

        class PropertyHolder
        {
            Dictionary<string, object> properties = new Dictionary<string, object>();
            Action<string> callback;

            internal PropertyHolder(Action<string> callback)
            {
                this.callback = callback;
            }

            internal bool Set<T>(T newValue, string propertyName)
            {
                var oldValue = Get<T>(propertyName);
                if (!EqualityComparer<T>.Default.Equals(newValue, oldValue))
                {
                    properties[propertyName] = newValue;
                    callback(propertyName);
                    return true;
                }
                return false;
            }

            internal T Get<T>(string propertyName)
            {
                return properties
                    .Where(s => s.Key == propertyName)
                    .Select(s => (T)s.Value)
                    .FirstOrDefault();
            }
        }
    }
}