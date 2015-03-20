using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reactive.Linq;
using System.Threading;
using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using System.Reactive.Concurrency;
using System.Collections.Generic;

namespace JoyReactor.Core.ViewModels
{
    public class TagInformationViewModel : ViewModel
    {
        public ObservableCollection<ItemViewModel> Items { get; } = new ObservableCollection<ItemViewModel>();

        IDisposable subscrition;

        public TagInformationViewModel()
        {
            MessengerInstance.Register<TagsViewModel.SelectTagMessage>(
                this, m => ChangeCurrentTag(m.Id));
        }

        public void Initialize() {
            ChangeCurrentTag(ID.Factory.New(ID.IdConst.ReactorGood));
        }

        public void ChangeCurrentTag(ID currentTagId)
        {
            Items.Clear();
            var sub = new TagCollectionModel()
                .GetLinkedTags(currentTagId)
                .ObserveOn(UiScheduler)
                .Subscribe(sections => Items.ReplaceAll(ConvertTagGroupsToItems(sections)));
            ChangeTagSubscription(sub);
        }

        IEnumerable<ItemViewModel> ConvertTagGroupsToItems(ICollection<TagGroup> sections)
        {
            return sections
                .SelectMany(s => s.Tags.Select(a => new { section = s, item = a }))
                .Select(s => new ItemViewModel(s.section, s.item));
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

        public class ItemViewModel
        {
            public string Group { get; private set; }

            public string Title { get; private set; }

            public string Image { get; private set; }

            public ItemViewModel(TagLinkedTag tag)
            {
                Group = tag.GroupName;
                Title = tag.Title;
                Image = tag.Image;
            }

            public ItemViewModel(TagGroup section, Tag tag) {
                Group = section.Title;
                Title = tag.Title;
                Image = tag.BestImage;
            }
        }
    }
}