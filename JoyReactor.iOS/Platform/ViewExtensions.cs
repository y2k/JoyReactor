using System.Windows.Input;
using UIKit;

namespace JoyReactor.iOS.Platform
{
    public static class ViewExtensions
    {
        public static void SetCommand(this UIButton instance, ICommand command)
        {
            instance.TouchUpInside += (sender, e) => command.Execute(null);
        }
    }
}