using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Media;

namespace JoyReactor.WP.View
{
    public sealed class ReactorImage : Panel
    {
        public int ImageWidth
        {
            get { return (int)base.GetValue(ImageWidthProperty); }
            set { base.SetValue(ImageWidthProperty, value); Invalidate(); }
        }
        public static readonly DependencyProperty ImageWidthProperty = DependencyProperty.Register("ImageWidth", typeof(int), typeof(ReactorImage), new PropertyMetadata(1));

        public int ImageHeight
        {
            get { return (int)base.GetValue(ImageHeightProperty); }
            set { base.SetValue(ImageHeightProperty, value); Invalidate(); }
        }
        public static readonly DependencyProperty ImageHeightProperty = DependencyProperty.Register("ImageHeight", typeof(int), typeof(ReactorImage), new PropertyMetadata(1));

        public string ImageSource
        {
            get { return (string)base.GetValue(ImageSourceProperty); }
            set { base.SetValue(ImageSourceProperty, value); Invalidate(); }
        }
        public static readonly DependencyProperty ImageSourceProperty = DependencyProperty.Register("ImageSource", typeof(int), typeof(ReactorImage), new PropertyMetadata(null));

        public ReactorImage()
        {
            Background = new SolidColorBrush(Colors.White);
        }

        private void Invalidate()
        {
            //
        }

        protected override Size MeasureOverride(Size availableSize)
        {
            var ps = new Size(availableSize.Width, availableSize.Width * ((float)ImageHeight / ImageWidth));

            // In our example, we just have one child.  
            // Report that our panel requires just the size of its only child. 
            foreach (UIElement child in Children)
            {
                child.Measure(ps);
            }

            //return new Size(ImageWidth, ImageHeight);
            return ps;
        }

        protected override Size ArrangeOverride(Size finalSize)
        {
            foreach (UIElement child in Children)
            {
                child.Arrange(new Rect(new Point(0, 0), child.DesiredSize));
            }
            return finalSize; // Returns the final Arranged size
        }
    }
}
