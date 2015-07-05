using System;
using System.Threading.Tasks;
using GalaSoft.MvvmLight.Command;

namespace JoyReactor.Core.ViewModels
{
    public class Command : RelayCommand
    {
        protected Action<object> action;

        public Command(Func<Task> asyncAction) : base(() => {})
        {
            action = async _ => await asyncAction();
        }

        public Command(Action action) : base(() => {})
        {
            this.action = _ => action();
        }

        public override void Execute(object parameter)
        {
            action(parameter);
        }

        public void Execute()
        {
            Execute(null);
        }
    }

    public class Command<T> : Command
    {
        public Command(Func<T, Task> asyncAction) : base(() => { })
        {
            action = async parameter => await asyncAction((T)parameter);
        }

        public Command(Action<T> action) : base(() => {})
        {
            this.action = s => action((T)s);
        }
    }
}