using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.ViewModel
{
    public class ProfileViewModel : ViewModelBase
    {
        private IProfileModel model = InjectService.Locator.GetInstance<IProfileModel>();

        private string _username;
        public string Username { get { return _username; } set { Set(ref _username, value); } }

        private bool _isAuthoried;
        public bool IsAuthorized { get { return _isAuthoried; } set { Set(ref _isAuthoried, value); } }

        private bool _isInProgress;
        public bool IsInProgress { get { return _isInProgress; } set { Set(ref _isInProgress, value); } }

        private string _rating;
        public string Rating { get { return _rating; } set { Set(ref _rating, value); } }

        private string _editUsername;
        public string EditUsername { get { return _editUsername; } set { Set(ref _editUsername, value); } }

        private string _editPassword;
        public string EditPassword { get { return _editPassword; } set { Set(ref _editPassword, value); } }

        public RelayCommand LoginCommand { get; set; }
        public RelayCommand LogoutCommand { get; set; }

        public ProfileViewModel()
        {
            //
            if (IsInDesignMode) {
                //
                Username = "test-user-1";
                Rating = "999";
            } else {
                LoginCommand = new RelayCommand(async () =>
                {
                    IsInProgress = true;
                    await model.LoginAsync(EditUsername, EditPassword);
                    Initialize();
                });
                LogoutCommand = new RelayCommand(async () => {
                    IsInProgress = true;
                    await model.LogoutAsync();
                    Initialize();
                });
                Initialize();
            }
        }

        private async void Initialize()
        {
            IsAuthorized = false;
            IsInProgress = true;

            var p = await model.GetCurrentProfileAsync();
            IsInProgress = false;
            if (p != null)
            {
                //
                IsAuthorized = true;
                Username = p.Username;
                Rating = "" + p.Rating;
            }
        }
    }
}