using System;
using System.Reflection;
using System.Windows.Controls;
using System.Windows.Media;
using System.Linq;
using System.Windows;
using JoyReactor.WP.Common;

namespace JoyReactor.WP.View
{
    public partial class TagImage : UserControl
    {
        private static readonly Random GlobalRandom = new Random();
        //private static readonly Color[] GlobalColors = GetColors();
        private static readonly uint[] GlobalColors = new uint[] {
			0xFF000033, 0xFF000066, 0xFF000099, 0xFF0000CC, 0xFF0000EE,
			0xFF003300, 0xFF003333, 0xFF003366, 0xFF003399, 0xFF0033CC, 0xFF0033EE,
			0xFF006600, 0xFF006633, 0xFF006666, 0xFF006699, 0xFF0066CC, 0xFF0066EE,
			0xFF009900, 0xFF009933, 0xFF009966, 0xFF009999, 0xFF0099CC, 0xFF0099EE,
			0xFF00CC00, 0xFF00CC33, 0xFF00CC66, 0xFF00CC99, 0xFF00CCCC, 0xFF00CCEE,
			0xFF00EE00, 0xFF00EE33, 0xFF00EE66, 0xFF00EE99, 0xFF00EECC, 0xFF00EEEE,
			0xFF330000, 0xFF330033, 0xFF330066, 0xFF330099, 0xFF3300CC, 0xFF3300EE,
			0xFF333300, 0xFF333366, 0xFF333399, 0xFF3333CC, 0xFF3333EE,
			0xFF336600, 0xFF336633, 0xFF336666, 0xFF336699, 0xFF3366CC, 0xFF3366EE,
			0xFF339900, 0xFF339933, 0xFF339966, 0xFF339999, 0xFF3399CC, 0xFF3399EE,
			0xFF33CC00, 0xFF33CC33, 0xFF33CC66, 0xFF33CC99, 0xFF33CCCC, 0xFF33CCEE,
			0xFF33EE00, 0xFF33EE33, 0xFF33EE66, 0xFF33EE99, 0xFF33EECC, 0xFF33EEEE,
			0xFF660000, 0xFF660033, 0xFF660066, 0xFF660099, 0xFF6600CC, 0xFF6600EE,
			0xFF663300, 0xFF663333, 0xFF663366, 0xFF663399, 0xFF6633CC, 0xFF6633EE,
			0xFF666600, 0xFF666633, 0xFF666699, 0xFF6666CC, 0xFF6666EE,
			0xFF669900, 0xFF669933, 0xFF669966, 0xFF669999, 0xFF6699CC, 0xFF6699EE,
			0xFF66CC00, 0xFF66CC33, 0xFF66CC66, 0xFF66CC99, 0xFF66CCCC, 0xFF66CCEE,
			0xFF66EE00, 0xFF66EE33, 0xFF66EE66, 0xFF66EE99, 0xFF66EECC, 0xFF66EEEE,
			0xFF990000, 0xFF990033, 0xFF990066, 0xFF990099, 0xFF9900CC, 0xFF9900EE,
			0xFF993300, 0xFF993333, 0xFF993366, 0xFF993399, 0xFF9933CC, 0xFF9933EE,
			0xFF996600, 0xFF996633, 0xFF996666, 0xFF996699, 0xFF9966CC, 0xFF9966EE,
			0xFF999900, 0xFF999933, 0xFF999966, 0xFF9999CC, 0xFF9999EE,
			0xFF99CC00, 0xFF99CC33, 0xFF99CC66, 0xFF99CC99, 0xFF99CCCC, 0xFF99CCEE,
			0xFF99EE00, 0xFF99EE33, 0xFF99EE66, 0xFF99EE99, 0xFF99EECC, 0xFF99EEEE,
			0xFFCC0000, 0xFFCC0033, 0xFFCC0066, 0xFFCC0099, 0xFFCC00CC, 0xFFCC00EE,
			0xFFCC3300, 0xFFCC3333, 0xFFCC3366, 0xFFCC3399, 0xFFCC33CC, 0xFFCC33EE,
			0xFFCC6600, 0xFFCC6633, 0xFFCC6666, 0xFFCC6699, 0xFFCC66CC, 0xFFCC66EE,
			0xFFCC9900, 0xFFCC9933, 0xFFCC9966, 0xFFCC9999, 0xFFCC99CC, 0xFFCC99EE,
			0xFFCCCC00, 0xFFCCCC33, 0xFFCCCC66, 0xFFCCCC99, 0xFFCCCCEE,
			0xFFCCEE00, 0xFFCCEE33, 0xFFCCEE66, 0xFFCCEE99, 0xFFCCEECC, 0xFFCCEEEE,
			0xFFEE0000, 0xFFEE0033, 0xFFEE0066, 0xFFEE0099, 0xFFEE00CC, 0xFFEE00EE,
			0xFFEE3300, 0xFFEE3333, 0xFFEE3366, 0xFFEE3399, 0xFFEE33CC, 0xFFEE33EE,
			0xFFEE6600, 0xFFEE6633, 0xFFEE6666, 0xFFEE6699, 0xFFEE66CC, 0xFFEE66EE,
			0xFFEE9900, 0xFFEE9933, 0xFFEE9966, 0xFFEE9999, 0xFFEE99CC, 0xFFEE99EE,
			0xFFEECC00, 0xFFEECC33, 0xFFEECC66, 0xFFEECC99, 0xFFEECCCC, 0xFFEECCEE,
			0xFFEEEE00, 0xFFEEEE33, 0xFFEEEE66, 0xFFEEEE99, 0xFFEEEECC,
	    };

        public string ImageSource
        {
            get { return (string)GetValue(ImageSourceProperty); }
            set { SetValue(ImageSourceProperty, value); }
        }
        public static readonly DependencyProperty ImageSourceProperty = DependencyProperty.Register("ImageSource", typeof(string), typeof(TagImage), new PropertyMetadata(Invalidate));

        public string Title
        {
            get { return (string)GetValue(TitleProperty); }
            set { SetValue(TitleProperty, value); }
        }
        public static readonly DependencyProperty TitleProperty = DependencyProperty.Register("Title", typeof(string), typeof(TagImage), new PropertyMetadata(Invalidate));

        public TagImage()
        {
            InitializeComponent();
            LayoutRoot.DataContext = this;

            Invalidate();
        }

        private static Color[] GetColors()
        {
            return typeof(Colors)
                   .GetProperties(BindingFlags.Public | BindingFlags.Static)
                   .Select(s => (Color)s.GetValue(null))
                   .ToArray();
        }

        private void Invalidate()
        {
            var self = this;
            self.Image.Visibility = ImageSource == null ? Visibility.Collapsed : Visibility.Visible;
            self.StubBackground.Visibility = ImageSource == null ? Visibility.Visible : Visibility.Collapsed;
            self.Image.ImageSource = ImageSource;
            self.Stub.Text = FirstUpperChar(Title);

            if (ImageSource == null)
            {
                //
                var c = UIntToColor(GlobalColors[GlobalRandom.Next(GlobalColors.Length)]);
                self.StubBackground.Background = new SolidColorBrush(c);
                self.Stub.Foreground = new SolidColorBrush(InvertColor(c));
            }
        }

        private static string FirstUpperChar(string value)
        {
            return string.IsNullOrEmpty(value) ? null : value.Substring(0, 1).ToUpper();
        }

        private static Color UIntToColor(uint color)
        {
            byte a = (byte)(color >> 24);
            byte r = (byte)(color >> 16);
            byte g = (byte)(color >> 8);
            byte b = (byte)(color >> 0);
            return Color.FromArgb(a, r, g, b);
        }

        private static Color InvertColor(Color c)
        {
            var h = HslColor.FromColor(c);
            h.L = 1 - h.L;
            h.H = (h.H + 180) % 360;
            return h.ToColor();
        }

        private static void Invalidate(DependencyObject d, DependencyPropertyChangedEventArgs e)
        {
            ((TagImage)d).Invalidate();
        }
    }
}