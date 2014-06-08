using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using JoyReactor.WP.Resources;
using JoyReactor.WP.ViewModel;

namespace JoyReactor.WP.View
{
    public partial class MainPage : BasePage
    {
        // Constructor
        public MainPage()
        {
            InitializeComponent();

            // Sample code to localize the ApplicationBar
            BuildLocalizedApplicationBar();
        }

        private void ListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ((MainViewModel)DataContext).OpenTagCommand.Execute(e.AddedItems[0]);
        }

        private void ApplicationBarMenuItem_Click(object sender, EventArgs e)
        {
            //((MainViewModel)DataContext).OpenProfileCommand.Execute(null);
            NavigationService.Navigate(new Uri("/View/ProfilePage.xaml", UriKind.Relative));
        }

        private void ApplicationBarMenuItem_Click_1(object sender, EventArgs e)
        {
            // TODO Navigate to setttings
        }

        private void ListBox_SelectionChanged_1(object sender, SelectionChangedEventArgs e)
        {
            ((MainViewModel)DataContext).OpenPostCommand.Execute(e.AddedItems[0]);
        }

        // Sample code for building a localized ApplicationBar
        private void BuildLocalizedApplicationBar()
        {
    //<phone:PhoneApplicationPage.ApplicationBar>
    //    <shell:ApplicationBar Mode="Minimized">
    //        <shell:ApplicationBar.MenuItems>
    //            <shell:ApplicationBarMenuItem Text="Profile" Click="ApplicationBarMenuItem_Click" />
    //            <shell:ApplicationBarMenuItem Text="Settings" Click="ApplicationBarMenuItem_Click_1" />
    //        </shell:ApplicationBar.MenuItems>
    //    </shell:ApplicationBar>
    //</phone:PhoneApplicationPage.ApplicationBar>
            
            // Set the page's ApplicationBar to a new instance of ApplicationBar.
            ApplicationBar = new ApplicationBar { Mode = ApplicationBarMode.Minimized };

            // Create a new button and set the text value to the localized string from AppResources.
            //ApplicationBarIconButton appBarButton = new ApplicationBarIconButton(new Uri("/Assets/AppBar/appbar.add.rest.png", UriKind.Relative));
            //appBarButton.Text = AppResources.AppBarButtonText;
            //ApplicationBar.Buttons.Add(appBarButton);

            // Create a new menu item with the localized string from AppResources.
            var i = new ApplicationBarMenuItem(AppResources.MenuProfile);
            i.Click += ApplicationBarMenuItem_Click;
            ApplicationBar.MenuItems.Add(i);

            i = new ApplicationBarMenuItem(AppResources.MenuSettings);
            i.Click += ApplicationBarMenuItem_Click_1;
            ApplicationBar.MenuItems.Add(i);
        }
    }
}