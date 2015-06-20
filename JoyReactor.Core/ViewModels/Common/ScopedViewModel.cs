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
    }
}