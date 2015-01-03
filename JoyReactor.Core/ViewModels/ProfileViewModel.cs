using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Helper;
using System;
using System.Reactive.Linq;
using System.Threading;
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

        IDisposable subscription;

        public ProfileViewModel()
        {
            LogoutCommand = new FixRelayCommand(
                async () => await new ProfileOperation().LogoutAsync());
        }

        public void Initialize()
        {
            IsLoading = true;
            subscription = ObservableFactory
                .IntervalAsync(TimeSpan.FromMinutes(1), GetMyProfile)
                .ObserveOn(SynchronizationContext.Current)
                .Subscribe(UpdateProfile);
        }

        async Task<MyProfileInformation> GetMyProfile()
        {
            var profile = new MyProfileInformation();
            await profile.LoadAsync();
            return profile;
        }

        void UpdateProfile(MyProfileInformation profile)
        {
            IsLoading = false;
            if (!profile.IsValid)
            {
                MessengerInstance.Send(new NavigateToLoginMessage());
            }
            else
            {
                Username = profile.Username;
                Rating = profile.Rating;
            }
        }

        public override void Cleanup()
        {
            base.Cleanup();
            subscription?.Dispose();
        }

        public class NavigateToLoginMessage
        {
        }
    }
}