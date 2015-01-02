using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.ViewModels
{
	public class ProfileViewModel : ViewModelBase
	{
		#region Properties

		bool _isLoading;

		public bool IsLoading {
			get { return _isLoading; }
			set { Set (ref _isLoading, value); }
		}

		string _avatar;

		public string Avatar {
			get { return _avatar; }
			set { Set (ref _avatar, value); }
		}

		string _username;

		public string Username {
			get { return _username; }
			set { Set (ref _username, value); }
		}

		float _rating;

		public float Rating {
			get { return _rating; }
			set { Set (ref _rating, value); }
		}

		#endregion

		public RelayCommand LogoutCommand { get; set; }

		public ProfileViewModel ()
		{
			LogoutCommand = new FixRelayCommand (
				async () => await new ProfileOperation ().LogoutAsync ());
		}

		public async void Initialize ()
		{
			IsLoading = true;
			var profile = new MyProfileInformation ();
			await profile.LoadAsync ();
			if (!profile.IsValid) {
				MessengerInstance.Send (new NavigateToLoginMessage ());
			} else {
				Username = profile.Username;
				Rating = profile.Rating;
			}
			IsLoading = false;
		}

		public class NavigateToLoginMessage
		{
		}
	}
}