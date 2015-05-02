using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core;
using JoyReactor.Core.ViewModels;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace JoyReactor.Windows.Views
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            Messenger.Default.Send(new TagsViewModel.SelectTagMessage { Id = GetDefaultListId() });
            Messenger.Default.Register<PostNavigationMessage>(this, m =>
                Frame.Navigate(typeof(PostPage), m.PostId));
        }

        protected override void OnNavigatingFrom(NavigatingCancelEventArgs e)
        {
            base.OnNavigatingFrom(e);
            Messenger.Default.Unregister(this);
        }

        private ID GetDefaultListId()
        {
            return ID.Factory.New(ID.IdConst.ReactorGood);
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            LeftPanel.Visibility = LeftPanel.Visibility == Visibility.Visible ? Visibility.Collapsed : Visibility.Visible;
        }
    }
}