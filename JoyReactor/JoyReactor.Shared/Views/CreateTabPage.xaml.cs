using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.ViewModels;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace JoyReactor.Views
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class CreateTabPage : Page
    {
        public CreateTabPage()
        {
            this.InitializeComponent();
        }

        public static void ShowAsDialog()
        {
            var page = new CreateTabPage();
            page.Width = Window.Current.Bounds.Width;
            page.Height = Window.Current.Bounds.Height;

            var popup = new Popup { Child = page, IsOpen = true };

            Messenger.Default.Register<CreateTagViewModel.CloseMessage>(page, s =>
            {
                popup.IsOpen = false;
                Messenger.Default.Unregister(page);
            });
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            Messenger.Default.Send(new CreateTagViewModel.CloseMessage());
        }
    }
}