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

        public string UserName
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

        int _stars;

        public int Stars
        {
            get { return _stars; }
            set { Set(ref _stars, value); }
        }

        float _nextStarProgress;

        public float NextStarProgress
        {
            get { return _nextStarProgress; }
            set { Set(ref _nextStarProgress, value); }
        }

        #endregion

        public RelayCommand LogoutCommand { get; set; }

        IProfileService service = ServiceLocator.Current.GetInstance<IProfileService>();

        public ProfileViewModel()
        {
            LogoutCommand = new Command(Logout);
        }

        public async Task Initialize()
        {
            IsLoading = true;
            try
            {
                var profile = await service.GetMyProfile();
                UserName = profile.UserName;
                Avatar = profile.UserImage;
                Rating = profile.Rating;
                Stars = profile.Stars;
                NextStarProgress = profile.NextStarProgress;
            }
            catch (NotLogedException)
            {
                NavigateToLogin();
            }
            IsLoading = false;
        }

        async void Logout()
        {
            await service.Logout();
            NavigateToLogin();
        }

        void NavigateToLogin()
        {
            MessengerInstance.Send(new NavigateToLoginMessage());
        }

        public class NavigateToLoginMessage
        {
        }
    }
}