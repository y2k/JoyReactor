using GalaSoft.MvvmLight.Messaging;
using JoyReactor.WP.Common;
using Microsoft.Phone.Controls;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.View
{
    public class BasePage : PhoneApplicationPage
    {
        protected override void OnNavigatedTo(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            Messenger.Default.Register<NavigationMessage>(this, s =>
            {
                var n = "/View/" + s.ViewModel.GetType().Name.Replace("ViewModel", "Page") + ".xaml";
                NavigationService.Navigate(new Uri(n, UriKind.Relative));
            });
        }

        protected override void OnNavigatedFrom(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedFrom(e);
            
            Messenger.Default.Unregister(this);
        }
    }
}
