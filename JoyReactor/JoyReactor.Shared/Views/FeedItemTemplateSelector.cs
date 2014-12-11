using JoyReactor.Core.ViewModels;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace JoyReactor.Views
{
    public class FeedItemTemplateSelector : DataTemplateSelector
    {
        public DataTemplate ContentTemplate { get; set; }
        public DataTemplate DividerTemplate { get; set; }

        protected override DataTemplate SelectTemplateCore(object item)
        {
            return item is FeedViewModel.DividerViewModel
                ? DividerTemplate
                : ContentTemplate;
        }
    }
}