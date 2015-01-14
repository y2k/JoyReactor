using JoyReactor.Core.Model.DTO;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace JoyReactor.Views
{
    public class PrivateMessageTemplateSelector : DataTemplateSelector
    {
        public DataTemplate InboxTempalte { get; set; }
        public DataTemplate OutboxTempate { get; set; }

        protected override DataTemplate SelectTemplateCore(object item)
        {
            return IsInbox(item) ? InboxTempalte : OutboxTempate;
        }

        private static bool IsInbox(object item)
        {
            return ((PrivateMessage)item).Mode == PrivateMessage.ModeInbox;
        }
    }
}