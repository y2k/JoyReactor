using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using System.Threading.Tasks;

namespace JoyReactor.Core.ViewModels
{
    public class LoginViewModel : ViewModelBase
	{
		#region Properties

		bool _isBusy;

		public bool IsBusy
		{
			get { return _isBusy; }
			set { Set(ref _isBusy, value); }
		}

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

		bool _hasError;

		public bool HasError
		{
			get { return _hasError; }
			set { Set(ref _hasError, value); }
		}

		public RelayCommand LoginCommand { get; set; }

		#endregion

		IProfileService service = ServiceLocator.Current.GetInstance<IProfileService>();

		public LoginViewModel()
		{
			LoginCommand = new FixRelayCommand(async () => await Login());
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