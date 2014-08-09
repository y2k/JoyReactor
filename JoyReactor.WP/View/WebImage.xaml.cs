using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.WP.Model;
using System.Windows.Media.Imaging;
using System.Threading;
using System.Threading.Tasks;

namespace JoyReactor.WP.View
{
    public partial class WebImage : UserControl
    {
        private IImageModel model = ServiceLocator.Current.GetInstance<IImageModel>();

        public string ImageSource
        {
            get { return (string)GetValue(ImageSourceProperty); }
            set { SetValue(ImageSourceProperty, value); }
        }
        public static readonly DependencyProperty ImageSourceProperty = DependencyProperty.Register("ImageSource", typeof(string), typeof(WebImage), new PropertyMetadata(Invalidate));

        public int ImageWidth
        {
            get { return (int)GetValue(ImageWidthProperty); }
            set { SetValue(ImageWidthProperty, value); }
        }
        public static readonly DependencyProperty ImageWidthProperty = DependencyProperty.Register("ImageWidth", typeof(int), typeof(WebImage), new PropertyMetadata(100));

        public WebImage()
        {
            InitializeComponent();
        }

        private static void Invalidate(DependencyObject d, DependencyPropertyChangedEventArgs e)
        {
            var self = (WebImage)d;
            var url = self.ImageSource == null ? null : new Uri(self.ImageSource);
            self.Image.Source = null;
            self.model.Load(self, url, self.ImageWidth, s => self.Image.Source = new BitmapImage(s.Image as Uri));
        }
    }
}