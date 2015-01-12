using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Reactive.Linq;
using System.Threading;

namespace JoyReactor.Core.ViewModels
{
    public class TagsViewModel : ViewModelBase, IDisposable
    {
        public ObservableCollection<TagItemViewModel> Tags { get; } = new ObservableCollection<TagItemViewModel>();

        int _selectedTag;

        IDisposable scheduledTask;

        public int SelectedTag
        {
            get { return _selectedTag; }
            set { Set(ref _selectedTag, value); }
        }

        public TagsViewModel()
        {
            PropertyChanged += TagsViewModel_PropertyChanged;
            if (!IsInDesignMode)
                Initialize();
        }

        void TagsViewModel_PropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            if (e.PropertyName == "SelectedTag" && SelectedTag >= 0)
                MessengerInstance.Send(new SelectTagMessage { Id = ID.Parser(Tags[SelectedTag].tag.TagId) });
        }

        void Initialize()
        {
            scheduledTask = new TagCollectionModel()
                .GetMainSubscriptions()
                .ObserveOn(SynchronizationContext.Current)
                .Subscribe(tags =>
                {
                    Tags.ReplaceAll(tags.Select(s => new TagItemViewModel(s)).ToList());
                    Tags.Insert(0, CreateFeaturedViewModel());
                });
        }

        public void Dispose()
        {
            scheduledTask?.Dispose();
        }

        private TagItemViewModel CreateFeaturedViewModel()
        {
            return new TagItemViewModel(new Tag
            {
                Title = "Featured",
                TagId = ID.Factory.New(ID.IdConst.ReactorGood).SerializeToString()
            });
        }

        public class TagItemViewModel : ViewModelBase
        {
            public ID TagId { get { return ID.Parser(tag.TagId); } }

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