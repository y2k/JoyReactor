using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.ViewModels
{
	public class TagsViewModel : ViewModelBase
	{
		public ObservableCollection<TagItemViewModel> Tags { get; } = new ObservableCollection<TagItemViewModel>();

		int _selectedTag;

		public int SelectedTag {
			get { return _selectedTag; }
			set { Set (ref _selectedTag, value); }
		}

		public TagsViewModel ()
		{
			PropertyChanged += TagsViewModel_PropertyChanged;
			if (!IsInDesignMode)
				LoadTag ();
		}

		TagCollectionModel model = new TagCollectionModel ();

		void TagsViewModel_PropertyChanged (object sender, PropertyChangedEventArgs e)
		{
			if (e.PropertyName == "SelectedTag")
				MessengerInstance.Send (new SelectTagMessage { Id = ID.Parser (Tags [SelectedTag].tag.TagId) });
		}

		async void LoadTag ()
		{
			var tags = await model.GetMainSubscriptionsAsync ();
			Tags.ReplaceAll (tags.Select (s => new TagItemViewModel (s)).ToList ());
		}

		public class TagItemViewModel : ViewModelBase
		{
			public ID TagId { get { return ID.Parser (tag.TagId); } }

			public string Title { get { return tag.Title; } }

			public string Image { get { return tag.BestImage; } }

			internal Tag tag;

			public TagItemViewModel (Tag tag)
			{
				this.tag = tag;
			}
		}
	}
}