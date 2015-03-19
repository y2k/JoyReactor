using GalaSoft.MvvmLight;

namespace JoyReactor.Core.ViewModels
{
    public class SettingsViewModel : ViewModelBase
    {
        string _host;

        public string Host
        {
            get { return _host; }
            set { Set(ref _host, value); }
        }

        bool _useAppProxy;

        public bool UseAppProxy
        {
            get { return _useAppProxy; }
            set { Set(ref _useAppProxy, value); }
        }
    }
}