using System;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Controllers
{
	public class FeedController
	{
		PostCollectionModel model = new PostCollectionModel();
		PostCollectionState data;
		bool syncInProgress;
		ID id;

		public Action InvalidateUiCallback { get; set; }

		public bool SyncInProgress {
			get { return syncInProgress; }
		}

		public List<Post> Posts {
			get { return data?.Posts; }
		}

		public bool HasNewItems {
			get {  return data?.NewItemsCount > 0; }
		}

		public FeedController (ID id)
		{
			LoadFirstPage (id);
		}

		async void LoadFirstPage (ID newId)
		{
			id = newId;
			data = null;
			InvalidateUi ();

			data = await model.Get (id);
			syncInProgress = true;
			InvalidateUi ();

			await model.SyncFirstPage (id);
			data = await model.Get (id);
			syncInProgress = false;
			InvalidateUi ();
		}

		void InvalidateUi ()
		{
			InvalidateUiCallback?.Invoke();
		}

		public async void OnRefreshInvoked ()
		{
			syncInProgress = true;
			InvalidateUi ();

			if (data.NewItemsCount > 0) {
				await model.ApplyNewItems (id);
			} else {
				await model.Reset (id);
				await model.SyncFirstPage (id);
			}
			data = await model.Get (id);
			syncInProgress = false;
			InvalidateUi ();
		}

		public async void OnApplyButtonClicked ()
		{
			await model.ApplyNewItems (id);
			data = await model.Get (id);
			InvalidateUi ();
		}

		public async void OnButtonMoreClicked ()
		{
			syncInProgress = true;
			InvalidateUi ();
			await model.SyncNextPage (id);
			data = await model.Get (id);
			syncInProgress = false;
			InvalidateUi ();
		}

		public void OnChangeCurrentListId(ID newId){
			LoadFirstPage (newId);
		}
	}
}