using GalaSoft.MvvmLight.Messaging;
using JoyReactor.WP.Common;
using JoyReactor.WP.ViewModel;
using Microsoft.Phone.Controls;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
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
                var n = s.ViewModel != null
                    ? "/View/" + s.ViewModel.GetType().Name.Replace("ViewModel", "Page") + ".xaml"
                    : "/View/" + s.Target.Name.Replace("ViewModel", "Page") + ".xaml";
                NavigationService.Navigate(new Uri(UrlFromParams(n, s.Args), UriKind.Relative));
            });

            if (DataContext is BaseViewModel) ((BaseViewModel)DataContext).InitializeWithBundle(ParamsFromUrl(e.Uri));            
        }

        protected override void OnNavigatedFrom(System.Windows.Navigation.NavigationEventArgs e)
        {
            base.OnNavigatedFrom(e);
            
            Messenger.Default.Unregister(this);
        }

        private static IDictionary<string, string> ParamsFromUrl(Uri url)
        {
            return Regex.Matches("" + url, "([^?&]+)=([^&]+)").Cast<Match>().ToDictionary(s => Uri.UnescapeDataString(s.Groups[1].Value), s => Uri.UnescapeDataString(s.Groups[2].Value));
        }

        private static string UrlFromParams(string baseUrl, IDictionary<string, string> args) {
            var p = new StringBuilder();
            if (args != null && args.Count > 0) {
                foreach (var s in args) {
                    p.Append(p.Length == 0 ? "?" : "&");
                    p.Append(Uri.EscapeDataString(s.Key)).Append("=").Append(Uri.EscapeDataString(s.Value));
                }
            }
            return baseUrl + p;
        }
    }
}