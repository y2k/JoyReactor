using System;
using Windows.Foundation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace JoyReactor.Views
{
    public class FixedAspectPanel : Panel
    {
        public int MaxChildWidth
        {
            get { return (int)GetValue(ImageWidthProperty); }
            set { SetValue(ImageWidthProperty, value); }
        }
        public static readonly DependencyProperty ImageWidthProperty = DependencyProperty.Register("MaxChildWidth", typeof(int), typeof(FixedAspectPanel), new PropertyMetadata(1));

        public int MaxChildHeight
        {
            get { return (int)GetValue(ImageHeightProperty); }
            set { SetValue(ImageHeightProperty, value); }
        }
        public static readonly DependencyProperty ImageHeightProperty = DependencyProperty.Register("MaxChildHeight", typeof(int), typeof(FixedAspectPanel), new PropertyMetadata(1));

        public double MaxAspect
        {
            get { return (double)GetValue(MaxAspectProperty); }
            set { SetValue(MaxAspectProperty, value); }
        }
        public static readonly DependencyProperty MaxAspectProperty = DependencyProperty.Register("MaxAspect", typeof(double), typeof(FixedAspectPanel), new PropertyMetadata(0));


        public FixedAspectPanel()
        {
            MaxChildWidth = 1;
            MaxChildHeight = 1;
        }

        protected override Size MeasureOverride(Size availableSize)
        {
            Size resultSize;
            if (MaxChildWidth * MaxChildHeight > 0)
            {
                var aspect = (double)MaxChildHeight / MaxChildWidth;
                if (MaxAspect > 0)
                    aspect = Math.Min(MaxAspect, aspect);

                resultSize = new Size(availableSize.Width, availableSize.Width * aspect);
            }

            foreach (var c in Children)
                c.Measure(resultSize);
            return resultSize;
        }

        protected override Size ArrangeOverride(Size finalSize)
        {
            foreach (var c in Children)
                c.Arrange(new Rect(new Point(0, 0), finalSize));
            return finalSize;
        }
    }
}