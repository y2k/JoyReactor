using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.ViewModels
{
	public class LoginViewModel : ViewModelBase
	{
        #region Properties

        bool _isBusy;

		public bool IsBusy {
			get { return _isBusy; }
			set { Set (ref _isBusy, value); }
		}

		string _password;

		public string Password {
			get { return _password; }
			set { Set (ref _password, value); }
		}

		string _username;

		public string Username {
			get { return _username; }
			set { Set (ref _username, value); }
		}

		bool _hasError;

		public bool HasError {
			get { return _hasError; }
			set { Set (ref _hasError, value); }
		}

		public RelayCommand LoginCommand { get; set; }

        #endregion

        public LoginViewModel ()
		{
			LoginCommand = new FixRelayCommand (Login);
		}

		async void Login ()
		{
			HasError = false;
			IsBusy = true;
			try {
				await new ProfileOperation ().LoginAsync (Username, Password);
				MessengerInstance.Send (new NavigateToProfileMessage ());
			} catch {
				HasError = true;
			}
			IsBusy = false;
		}

		public class NavigateToProfileMessage
		{
		}
	}
}