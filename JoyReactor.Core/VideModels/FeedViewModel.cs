using System.Collections.ObjectModel;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Threading.Tasks;

namespace JoyReactor.Core.VideModels
{
	public class FeedViewModel : ViewModelBase
	{
		#region Commands

		public RelayCommand RefreshCommand { get; set; }

		public RelayCommand MoreCommand { get; set; }

		public RelayCommand ApplyCommand { get; set; }

		public RelayCommand<ID> ChangeCurrentListIdCommand { get; set; }

		#endregion

		#region Properties

		public ObservableCollection<Post> Posts { get; } = new ObservableCollection<Post>();

		bool _isBusy;

		public bool IsBusy {
			get { return _isBusy; }
			set { Set (ref _isBusy, value); }
		}

		bool _hasNewItems;

		public bool HasNewItems {
			get { return _hasNewItems; }
			set { Set (ref _hasNewItems, value); }
		}

		int _dividerPosition;

		public int DividerPosition {
			get { return _dividerPosition; }
			set { Set (ref _dividerPosition, value); }
		}

		#endregion

		PostCollectionModel model = new PostCollectionModel ();
		ID id;

		public FeedViewModel (ID id)
		{
			DividerPosition = -1;

			RefreshCommand = new RelayCommand (OnRefreshInvoked);
			MoreCommand = new RelayCommand (OnButtonMoreClicked);
			ApplyCommand = new RelayCommand (OnApplyButtonClicked);
			ChangeCurrentListIdCommand = new RelayCommand<ID> (OnChangeCurrentListId);
			LoadFirstPage (id);
		}

		async void LoadFirstPage (ID newId)
		{
			id = newId;

			IsBusy = true;
			await ReloadDataFromDatabase ();
			await model.SyncFirstPage (id);
			await ReloadDataFromDatabase ();
			IsBusy = false;
		}

		async Task ReloadDataFromDatabase ()
		{
			var data = await model.Get (id);
			Posts.ReplaceAll (data.Posts);
			HasNewItems = data.NewItemsCount > 0;
			DividerPosition = data.Posts.Count > 0 ? data.DividerPosition : -1;
		}

		async void OnRefreshInvoked ()
		{
			IsBusy = true;
			if (HasNewItems) {
				await model.ApplyNewItems (id);
			} else {
				await model.Reset (id);
				await model.SyncFirstPage (id);
			}
			await ReloadDataFromDatabase ();
			IsBusy = false;
		}

		async void OnApplyButtonClicked ()
		{
			await model.ApplyNewItems (id);
			await ReloadDataFromDatabase ();
		}

		async void OnButtonMoreClicked ()
		{
			await model.SyncNextPage (id);
			await ReloadDataFromDatabase ();
		}

		void OnChangeCurrentListId (ID newId)
		{
			LoadFirstPage (newId);
		}
	}
}