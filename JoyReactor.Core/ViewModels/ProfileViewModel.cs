using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using System.Threading.Tasks;

namespace JoyReactor.Core.ViewModels
{
    public class ProfileViewModel : ViewModelBase
    {
        #region Properties

        bool _isLoading;

        public bool IsLoading
        {
            get { return _isLoading; }
            set { Set(ref _isLoading, value); }
        }

        string _avatar;

        public string Avatar
        {
            get { return _avatar; }
            set { Set(ref _avatar, value); }
        }

        string _username;

        public string Username
        {
            get { return _username; }
            set { Set(ref _username, value); }
        }

        float _rating;

        public float Rating
        {
            get { return _rating; }
            set { Set(ref _rating, value); }
        }

        #endregion

        public RelayCommand LogoutCommand { get; set; }

        IProfileService service = ServiceLocator.Current.GetInstance<IProfileService>();

        public ProfileViewModel()
        {
            LogoutCommand = new FixRelayCommand(async () => await service.Logout());
        }

        public async Task Initialize()
        {
            IsLoading = true;
            try
            {
                var profile = await service.GetMyProfile();
                Username = profile.Username;
                Rating = profile.Rating;
            }
            catch (NotLogedException)
            {
                MessengerInstance.Send(new NavigateToLoginMessage());
            }
            IsLoading = false;
        }

        public class NavigateToLoginMessage
        {
        }
    }
}