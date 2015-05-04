using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.ViewModels;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;

namespace JoyReactor.Windows.Views
{
    public sealed partial class LoginPage : Page
    {
        public LoginPage()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            Messenger.Default.Register<LoginViewModel.NavigateToProfileMessage>(
                this, _ => Frame.Navigate(typeof(ProfilePage)));
        }

        protected override void OnNavigatingFrom(NavigatingCancelEventArgs e)
        {
            Messenger.Default.Unregister(this);
        }
    }
}