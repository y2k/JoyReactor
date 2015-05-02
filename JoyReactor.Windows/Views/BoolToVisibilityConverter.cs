using System;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Data;

namespace JoyReactor.Windows.Views
{
    class BoolToVisibilityConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, string language)
        {
            return "invert".Equals(parameter)
                ? true.Equals(value) ? Visibility.Collapsed : Visibility.Visible
                : true.Equals(value) ? Visibility.Visible : Visibility.Collapsed;
        }

        public object ConvertBack(object value, Type targetType, object parameter, string language)
        {
            throw new NotImplementedException();
        }
    }
}