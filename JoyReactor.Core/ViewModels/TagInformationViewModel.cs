using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reactive.Linq;
using System.Threading;

namespace JoyReactor.Core.ViewModels
{
    public class TagInformationViewModel : ViewModelBase
    {
        public ObservableCollection<ItemViewModel> Items { get; } = new ObservableCollection<ItemViewModel>();

        IDisposable subscrition;

        public TagInformationViewModel()
        {
            MessengerInstance.Register<SelectTagMessage>(this, m => ChangeCurrentTag(m.Id));
        }

        public void ChangeCurrentTag(ID currentTagId)
        {
            Items.Clear();

            var sub = new TagCollectionModel()
                .GetTagLinkedTags(currentTagId)
                .ObserveOn(SynchronizationContext.Current)
                .Subscribe(tags => Items.ReplaceAll(from s in tags select new ItemViewModel(s)));
            ChangeTagSubscription(sub);
        }

        public override void Cleanup()
        {
            base.Cleanup();
            ChangeTagSubscription(null);
        }

        private void ChangeTagSubscription(IDisposable newSubscription)
        {
            subscrition?.Dispose();
            subscrition = newSubscription;
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