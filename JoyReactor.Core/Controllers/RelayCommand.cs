using System;
using System.Windows.Input;

namespace JoyReactor.Core.Controllers
{
	public class RelayCommand : ICommand
	{
		Action action;

		public RelayCommand (Action action)
		{
			this.action = action;
		}

		public event EventHandler CanExecuteChanged;

		public bool CanExecute (object parameter)
		{
			return true;
		}

		public void Execute (object parameter)
		{
			action ();
		}
	}
}