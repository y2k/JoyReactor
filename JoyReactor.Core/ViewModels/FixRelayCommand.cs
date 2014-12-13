using System;
using GalaSoft.MvvmLight.Command;

namespace JoyReactor.Core.ViewModels
{
	public class FixRelayCommand : RelayCommand
	{
		Action action;

		public FixRelayCommand (Action action) : base (() => {
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