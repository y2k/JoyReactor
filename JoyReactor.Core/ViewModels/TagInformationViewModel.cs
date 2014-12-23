using System.Collections.ObjectModel;
using System.Threading.Tasks;
using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.ViewModels
{
    public class TagInformationViewModel : ViewModelBase
    {
        public ObservableCollection<ItemViewModel> Items { get; } = new ObservableCollection<ItemViewModel>();

        public TagInformationViewModel()
        {
            MessengerInstance.Register<SelectTagMessage>(this, async m => await ChangeCurrentTag(m.Id));
        }

        public async Task ChangeCurrentTag(ID currentTagId)
        {
            Items.Clear();

            var model = new TagCollectionModel();
            var tags = await model.GetTagLinkedTagsAsync(currentTagId);

            foreach (var s in tags)
                Items.Add(new ItemViewModel(s));
        }

        public class ItemViewModel : ViewModelBase
        {
            public string Group { get { return tag.GroupName; } }

            public string Title { get { return tag.Title; } }

            public string Image { get { return tag.Image; } }

            readonly TagLinkedTag tag;

            public ItemViewModel(TagLinkedTag tag)
            {
                this.tag = tag;
            }
        }
    }
}