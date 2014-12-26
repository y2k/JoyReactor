using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;

namespace JoyReactor.Core.ViewModels
{
    public class LoginViewModel : ViewModelBase
    {
        string _password;
        public string Password
        {
            get { return _password; }
            set { Set(ref _password, value); }
        }

        string _username;
        public string Username
        {
            get { return _username; }
            set { Set(ref _username, value); }
        }

        public RelayCommand LoginCommand { get; set; }

        public LoginViewModel()
        {
            LoginCommand = new FixRelayCommand(() => { });
        }
    }
}