using System;
using System.Windows.Input;
using JoyReactor.Core.Model;
using System.Threading.Tasks;

namespace JoyReactor.Core.Controllers
{
	public class CreateTagController
	{
		public string Name { get; set; } = "";

		public bool NameError { get; set; }

		public bool IsBusy { get; set; }

		public bool IsComplete { get; set; }

		public ICommand CreateCommand { get; set; }

		public Action InvalidateUiCallback { get; set; }

		public CreateTagController ()
		{
			CreateCommand = new RelayCommand (HandleCreateTag);
		}

		void HandleCreateTag ()
		{
			if (ValidTagName ())
				CreateTag ();
		}

		bool ValidTagName ()
		{
			Name = Name.Trim ();
			NameError = string.IsNullOrEmpty (Name);
			InvalidateUiCallback ();
			return !NameError;
		}

		async void CreateTag ()
		{
			IsBusy = true;
			InvalidateUiCallback ();

			await new PostModel ().CreateTag (Name);

			IsBusy = false;
			IsComplete = true;
			InvalidateUiCallback ();
		}
	}
}