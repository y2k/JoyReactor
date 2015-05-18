using System.Threading.Tasks;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model;
using System;

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

            var tag = new Tag
            {
                Title = Name.Trim(),
                TagId = ID.Factory.NewTag(Name.Trim().ToLower()).SerializeToString(),
                Flags = Tag.FlagShowInMain,
            };
            await new TagImageProvider(tag).LoadAsync();
            if (Uri.IsWellFormedUriString(tag.BestImage, UriKind.Absolute))
            {
                await new TagRepository().InsertIfNotExistsAsync(tag);
                await TagCollectionModel.InvalidateTagCollectionAsync();
                MessengerInstance.Send(new CloseMessage());
            }

            IsBusy = false;
        }

        public class CloseMessage
        {
        }

        internal interface IPostService
        {
            Task CreateTagAsync(string tagName);
        }
    }
}