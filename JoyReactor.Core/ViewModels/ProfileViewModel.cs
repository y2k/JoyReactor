using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.ViewModels
{
	public class ProfileViewModel : ViewModelBase
	{
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

		public RelayCommand LogoutCommand { get; set; }

		public ProfileViewModel ()
		{
			LogoutCommand = new FixRelayCommand (async () => {
				await new ProfileModel ().LogoutAsync ();
			});
		}

		public async void Initialize ()
		{
			IsLoading = true;
			var profile = await new ProfileModel ().GetCurrentProfileAsync ();
			if (profile == null) {
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