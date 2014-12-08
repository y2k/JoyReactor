using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.VideModels
{
	public class FeedViewModel : ViewModelBase
	{
		PostCollectionModel model = new PostCollectionModel ();
		PostCollectionState data;
		ID id;

		public RelayCommand RefreshCommand { get; set; }

		public RelayCommand MoreCommand { get; set; }

		public RelayCommand ApplyCommand { get; set; }

		public RelayCommand<ID> ChangeCurrentListIdCommand { get; set; }

		public ObservableCollection<Post> Posts { get; } = new ObservableCollection<Post>();

		bool _syncInProgress;

		public bool SyncInProgress {
			get { return _syncInProgress; }
			set { Set (ref _syncInProgress, value); }
		}

		bool _hasNewItems;

		public bool HasNewItems {
			get { return _hasNewItems; }
			set { Set (ref _hasNewItems, value); }
		}

		public FeedViewModel (ID id)
		{
			RefreshCommand = new RelayCommand (OnRefreshInvoked);
			MoreCommand = new RelayCommand (OnButtonMoreClicked);
			ApplyCommand = new RelayCommand (OnApplyButtonClicked);
			ChangeCurrentListIdCommand = new RelayCommand<ID> (OnChangeCurrentListId);
			LoadFirstPage (id);
		}

		async void LoadFirstPage (ID newId)
		{
			id = newId;
			data = null;

			data = await model.Get (id);
			Posts.ReplaceAll (data.Posts);
			SyncInProgress = true;

			await model.SyncFirstPage (id);
			data = await model.Get (id);
			Posts.ReplaceAll (data.Posts);
			SyncInProgress = false;
		}

		async void OnRefreshInvoked ()
		{
			SyncInProgress = true;
			if (data.NewItemsCount > 0) {
				await model.ApplyNewItems (id);
			} else {
				await model.Reset (id);
				await model.SyncFirstPage (id);
			}
			data = await model.Get (id);
			Posts.ReplaceAll (data.Posts);
			SyncInProgress = false;
		}

		async void OnApplyButtonClicked ()
		{
			await model.ApplyNewItems (id);
			data = await model.Get (id);
			Posts.ReplaceAll (data.Posts);
		}

		async void OnButtonMoreClicked ()
		{
			await model.SyncNextPage (id);
			data = await model.Get (id);
			Posts.ReplaceAll (data.Posts);
		}

		void OnChangeCurrentListId (ID newId)
		{
			LoadFirstPage (newId);
		}
	}
}