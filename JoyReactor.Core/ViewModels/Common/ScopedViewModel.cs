using System;
using System.Collections.Generic;
using JoyReactor.Core.ViewModels;

namespace JoyReactor.Core.ViewModels
{
    public class ScopedViewModel : ViewModel
    {
        public virtual void OnActivated()
        {
        }

        public virtual void OnDeactivated()
        {
            MessengerInstance.Unregister(this);
        }

        public class Scope
        {
            readonly List<ScopedViewModel> viewmodels = new List<ScopedViewModel>();

            public T New<T>() where T : ScopedViewModel
            {
                var vm = Activator.CreateInstance<T>();
                viewmodels.Add(vm);
                return vm;
            }

            public void OnActivated()
            {
                foreach (var s in viewmodels)
                    s.OnActivated();
            }

            public void OnDeactivated()
            {
                foreach (var s in viewmodels)
                    s.OnDeactivated();
            }
        }
    }
}