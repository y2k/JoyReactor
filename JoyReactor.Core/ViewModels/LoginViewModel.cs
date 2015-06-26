using System.Threading.Tasks;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.ViewModels
{
    public class LoginViewModel : ViewModel
    {
        public bool IsBusy { get { return Get<bool>(); } set { Set(value); } }

        public string Password { get { return Get<string>(); } set { Set(value); } }

        public string Username { get { return Get<string>(); } set { Set(value); } }

        public bool HasError { get { return Get<bool>(); } set { Set(value); } }

        public RelayCommand LoginCommand { get; set; }

        readonly IProfileService service = ServiceLocator.Current.GetInstance<IProfileService>();

        public LoginViewModel()
        {
            LoginCommand = new Command(Login);
        }

        public async Task Login()
        {
            HasError = false;
            IsBusy = true;
            try
            {
                await service.Login(Username, Password);
                MessengerInstance.Send(new NavigateToProfileMessage());
            }
            catch
            {
                HasError = true;
                IsBusy = false;
                MessengerInstance.Send(new LoginFailMessage());
            }
        }

        public class NavigateToProfileMessage
        {
        }

        public class LoginFailMessage
        {
        }
    }
}