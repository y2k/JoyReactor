using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.ViewModels;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;

namespace JoyReactor.Windows.Views
{
    public sealed partial class ProfilePage : Page
    {
        public ProfilePage()
        {
            this.InitializeComponent();
        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            Messenger.Default.Register<ProfileViewModel.NavigateToLoginMessage>(
                this, _ => Frame.Navigate(typeof(LoginPage)));
            await ((ProfileViewModel)DataContext).Initialize();
        }

        protected override void OnNavigatingFrom(NavigatingCancelEventArgs e)
        {
            Messenger.Default.Unregister(this);
        }
    }
}