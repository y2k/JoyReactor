using JoyReactor.Core.Model;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Imaging;

// The User Control item template is documented at http://go.microsoft.com/fwlink/?LinkId=234236

namespace JoyReactor.Windows.Views
{
    public sealed partial class ThumbnailImage : UserControl
    {
        public string Source
        {
            get { return (string)GetValue(SourceProperty); }
            set { SetValue(SourceProperty, value); }
        }
        public static readonly DependencyProperty SourceProperty =
            DependencyProperty.Register("Source", typeof(string), typeof(ThumbnailImage),
                new PropertyMetadata(null, (o, p) => ((ThumbnailImage)o).ReloadImage()));

        public int ThumbnailSize
        {
            get { return (int)GetValue(ThumbnailSizeProperty); }
            set { SetValue(ThumbnailSizeProperty, value); }
        }
        public static readonly DependencyProperty ThumbnailSizeProperty =
            DependencyProperty.Register("ThumbnailSize", typeof(int), typeof(ThumbnailImage),
                new PropertyMetadata(0, (o, p) => ((ThumbnailImage)o).ReloadImage()));

        public ThumbnailImage()
        {
            this.InitializeComponent();
        }

        private void ReloadImage()
        {
            if (Source != null)
            {
                new ImageRequest()
                    .SetUrl(Source)
                    .CropIn(ThumbnailSize)
                    .SetToken(this)
                    .Into<BitmapImage>(s => Image.Source = s);
            }
        }
    }
}