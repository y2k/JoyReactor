using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace JoyReactor.WP.View
{
    public class ColorImage : UserControl
    {
        public string Source
        {
            get { return (string)base.GetValue(SourceProperty); }
            set { base.SetValue(SourceProperty, value); InvalidateSource(); }
        }
        public static readonly DependencyProperty SourceProperty = DependencyProperty.Register("Source", typeof(int), typeof(ColorImage), new PropertyMetadata(null));

        public string ColorTitle
        {
            get { return (string)base.GetValue(ColorTitleProperty); }
            set { base.SetValue(ColorTitleProperty, value); }
        }
        public static readonly DependencyProperty ColorTitleProperty = DependencyProperty.Register("ColorTitle", typeof(int), typeof(ColorImage), new PropertyMetadata(null));

        public ColorImage()
        {
            var image = new Image { Stretch = Stretch.Fill };
        }

        private void InvalidateSource()
        {
            throw new NotImplementedException();
        }
    }
}