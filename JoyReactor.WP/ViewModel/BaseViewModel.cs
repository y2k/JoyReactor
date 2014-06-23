using GalaSoft.MvvmLight;
using JoyReactor.WP.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.ViewModel
{
    public class BaseViewModel : ViewModelBase
    {
        public virtual void InitializeWithBundle(IDictionary<string, string> state) { }

        internal void NavigateToViewModel(Type viewModelType, params object[] keyValueArgs)
        {
            var ag = new Dictionary<string, string>();
            for (int i = 0; i < keyValueArgs.Length; i += 2)
            {
                ag["" + keyValueArgs[i]] = "" + keyValueArgs[i + 1];
            }
            MessengerInstance.Send(new NavigationMessage { Target = viewModelType, Args = ag });
        }
    }
}
