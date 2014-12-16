using Windows.Foundation;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace JoyReactor.Views
{
    public class FixedAspectPanel : Panel
    {
        public int MaxChildWidth
        {
            get { return (int)base.GetValue(ImageWidthProperty); }
            set { base.SetValue(ImageWidthProperty, value); }
        }
        public static readonly DependencyProperty ImageWidthProperty = DependencyProperty.Register("MaxChildWidth", typeof(int), typeof(FixedAspectPanel), new PropertyMetadata(1));

        public int MaxChildHeight
        {
            get { return (int)base.GetValue(ImageHeightProperty); }
            set { base.SetValue(ImageHeightProperty, value); }
        }
        public static readonly DependencyProperty ImageHeightProperty = DependencyProperty.Register("MaxChildHeight", typeof(int), typeof(FixedAspectPanel), new PropertyMetadata(1));

        public FixedAspectPanel()
        {
            MaxChildWidth = 1;
            MaxChildHeight = 1;
        }

        protected override Size MeasureOverride(Size availableSize)
        {
            var s = MaxChildWidth * MaxChildHeight == 0
                ? new Size(0, 0)
                : new Size(availableSize.Width, ((availableSize.Width) / MaxChildWidth) * MaxChildHeight);
            foreach (var c in Children)
            {
                c.Measure(s);
            }
            return s;
        }

        protected override Size ArrangeOverride(Size finalSize)
        {
            foreach (var c in Children)
            {
                c.Arrange(new Rect(new Point(0, 0), finalSize));
            }
            return finalSize;
        }
    }
}