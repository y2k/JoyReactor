using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.ViewModels
{
    public class CreateTagViewModel : ViewModelBase
    {
        #region Properties

        string _name;
        public string Name
        {
            get { return _name; }
            set { Set(ref _name, value); }
        }

        bool _nameError;
        public bool NameError
        {
            get { return _nameError; }
            set { Set(ref _nameError, value); }
        }

        bool _isBusy;
        public bool IsBusy
        {
            get { return _isBusy; }
            set { Set(ref _isBusy, value); }
        }

        bool _isComplete;
        public bool IsComplete
        {
            get { return _isComplete; }
            set { Set(ref _isComplete, value); }
        }

        #endregion

        public RelayCommand CreateCommand { get; set; }

        public CreateTagViewModel()
        {
            CreateCommand = new RelayCommand(OnCreateTag);
        }

        void OnCreateTag()
        {
            if (ValidTagName())
                CreateTag();
        }

        bool ValidTagName()
        {
            NameError = string.IsNullOrWhiteSpace(Name);
            return !NameError;
        }

        async void CreateTag()
        {
            IsBusy = true;
            Name = Name.Trim();
            await new PostModel().CreateTag(Name);
            IsBusy = false;
            IsComplete = true;
            MessengerInstance.Send(new CloseMessage());
        }

        public class CloseMessage
        {
        }
    }
}