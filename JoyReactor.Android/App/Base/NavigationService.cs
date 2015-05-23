using System;
using Android.Content;
using GalaSoft.MvvmLight.Views;
using Uri = Android.Net.Uri;

namespace JoyReactor.Android.App.Base
{
    public class NavigationService : INavigationService
    {
        Context context;

        public NavigationService(Context context)
        {
            this.context = context;
        }

        public void GoBack()
        {
            throw new NotImplementedException();
        }

        public void NavigateTo(string pageKey)
        {
            if (System.Uri.IsWellFormedUriString(pageKey, UriKind.Absolute))
                context.StartActivity(new Intent(Intent.ActionView, Uri.Parse(pageKey)));
            else
                throw new NotImplementedException();
        }

        public void NavigateTo(string pageKey, object parameter)
        {
            throw new NotImplementedException();
        }

        public string CurrentPageKey
        {
            get
            {
                throw new NotImplementedException();
            }
        }
    }
}