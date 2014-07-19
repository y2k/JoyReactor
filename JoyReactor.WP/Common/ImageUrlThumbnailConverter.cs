using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;

namespace JoyReactor.WP.Common
{
    public class ImageUrlThumbnailConverter : IValueConverter
    {
        private IImageModel model = ServiceLocator.Current.GetInstance<IImageModel>();

        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            return value == null ? null : model.CreateThumbnailUrl("" + value, int.Parse("" + parameter));
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}