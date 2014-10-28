using System;
using System.Linq;
using System.Collections.Generic;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model
{
	public class FirstPagePostSorter
	{
		List<int> newUniqePostIds = new List<int> ();
		List<int> newDublicatePosts = new List<int> ();
		List<int> currentPostIds;
		ISQLiteConnection db;
		int tagId;

		public FirstPagePostSorter (ISQLiteConnection db, string tagId)
		{
			this.db = db;
			GetTagId (tagId);
			LoadPostIdsForTag ();
		}

		void GetTagId (string textTagId)
		{
			tagId = db.SafeExecuteScalar<int> (
				"SELECT Id " +
				"FROM tags " +
				"WHERE TagId = ?", textTagId);
		}

		void LoadPostIdsForTag ()
		{
			currentPostIds = db
				.SafeQuery<TagPost> ("SELECT PostId FROM tag_post WHERE TagId = ?", tagId)
				.Select (s => s.PostId)
				.ToList ();
		}

		public void AddNewPost (int newid)
		{
			if (currentPostIds.Contains (newid))
				newDublicatePosts.Add (newid);
			else
				newUniqePostIds.Add (newid);
		}

		public void SaveChanges ()
		{
			db.SafeRunInTransaction (() => {
				db.SafeExecute ("DELETE FROM tag_post WHERE TagId = ?", tagId);

				if (IsFirstExecution) {
					foreach (var id in newUniqePostIds)
						db.SafeInsert (new TagPost { 
							TagId = tagId, 
							PostId = id,
							Status = TagPost.StatusActual
						});
				} else {
					foreach (var id in newUniqePostIds)
						db.SafeInsert (new TagPost { 
							TagId = tagId, 
							PostId = id,
							Status = TagPost.StatusNew
						});
					foreach (var id in currentPostIds)
						db.SafeInsert (new TagPost {
							TagId = tagId,
							PostId = id, 
							Status = newDublicatePosts.Contains (id)
								? TagPost.StatusActual
								: TagPost.StatusOld,
						});
				}
			});
		}

		bool IsFirstExecution {
			get { return currentPostIds.Count <= 0; }
		}
	}
}