using System.Windows.Input;
using Android.Content;
using Android.Util;
using Android.Widget;

namespace JoyReactor.Android.Widget
{
    public class CommandButton : Button
    {
        public ICommand ClickCommand { get; set; }

        public object ClickCommandArgument { get; set; }

        public ICommand LongClickCommand { get; set; }

        public object LongClickCommandArgument { get; set; }

        public CommandButton(Context context, IAttributeSet attrs)
            : base(context, attrs)
        {
            Click += (sender, e) =>
            {
                if (ClickCommand != null)
                    ClickCommand.Execute(ClickCommandArgument);
            };
            LongClick += (sender, e) =>
            {
                if (ClickCommand != null)
                    LongClickCommand.Execute(LongClickCommandArgument);
            };
        }
    }
}