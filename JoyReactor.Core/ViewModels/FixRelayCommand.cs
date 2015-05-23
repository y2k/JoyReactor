using System;
using GalaSoft.MvvmLight.Command;

namespace JoyReactor.Core.ViewModels
{
	public class Command : RelayCommand
	{
		Action action;

		public Command (Action action) : base (() => {
			})
		{
			this.action = action;
		}

		public override void Execute (object parameter)
		{
			action ();
		}
	}
}