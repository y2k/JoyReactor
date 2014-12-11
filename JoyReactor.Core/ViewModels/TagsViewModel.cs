using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;

namespace JoyReactor.Core.ViewModels
{
    public class TagsViewModel : ViewModelBase
    {
        TagCollectionModel model = new TagCollectionModel();

        List<TagItemViewModel> _tags;
        public List<TagItemViewModel> Tags
        {
            get { return _tags; }
            set { Set(ref _tags, value); }
        }

        int _selectedTag;
        public int SelectedTag
        {
            get { return _selectedTag; }
            set { Set(ref _selectedTag, value); }
        }

        public TagsViewModel()
        {
            PropertyChanged += TagsViewModel_PropertyChanged;
            if (!IsInDesignMode)
                LoadTag();
        }

        private void TagsViewModel_PropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            if (e.PropertyName == "SelectedTag")
                MessengerInstance.Send(new SelectTagMessage { Id = ID.Parser(Tags[SelectedTag].tag.TagId) });
        }

        private async void LoadTag()
        {
            var tags = await model.GetMainSubscriptionsAsync();
            Tags = tags.Select(s => new TagItemViewModel(s)).ToList();
        }

        public class TagItemViewModel : ViewModelBase
        {
            public string Title { get { return tag.Title; } }
            public string Image { get { return tag.BestImage; } }

            internal Tag tag;

            public TagItemViewModel(Tag tag)
            {
                this.tag = tag;
            }
        }
    }
}