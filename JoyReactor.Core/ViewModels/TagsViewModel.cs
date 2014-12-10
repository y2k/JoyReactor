using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Linq;

namespace JoyReactor.Core.ViewModels
{
    public class TagsViewModel : ViewModelBase
    {
        TagCollectionModel model = new TagCollectionModel();

        private List<TagItemViewModel> _tags;
        public List<TagItemViewModel> Tags
        {
            get { return _tags; }
            set { Set(ref _tags, value); }
        }

        public TagsViewModel()
        {
            if (!IsInDesignMode)
                LoadTag();
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

            Tag tag;

            public TagItemViewModel(Tag tag)
            {
                this.tag = tag;
            }
        }
    }
}