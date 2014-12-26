using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.ViewModels
{
    public class ProfileViewModel : ViewModelBase
    {
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

        public RelayCommand LogoutCommand { get; set; }

        public void Initialize()
        {
            //
        }
    }
}