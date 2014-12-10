using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using SQLite.Net;
using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model
{
    public class NextPagePostSorter
	{
		List<int> currentActualPostIds = new List<int> ();
		List<int> currentOldPostIds = new List<int> ();
		List<int> newPostIds = new List<int> ();
		SQLiteConnection db;
		int tagId;

		public NextPagePostSorter (SQLiteConnection db, string tagId)
		{
			this.db = db;
			GetTagId (tagId);
			LoadPostIdsForTag ();
		}

		void GetTagId (string textTagId)
		{
			tagId = db.SafeExecuteScalar<int> ("SELECT Id FROM tags WHERE TagId = ?", textTagId);
		}

		void LoadPostIdsForTag ()
		{
			var links = db.SafeQuery<TagPost> ("SELECT PostId, Status FROM tag_post WHERE TagId = ?", tagId);
			foreach (var s in links) {
				switch (s.Status) {
				case TagPost.StatusActual:
					currentActualPostIds.Add (s.PostId);
					break;
				case TagPost.StatusOld:
					currentOldPostIds.Add (s.PostId);
					break;
				default:
					throw new InvalidOperationException ();
				}
			}
		}

		public void AddNewPost (int newid)
		{
			if (!currentActualPostIds.Contains (newid))
				newPostIds.Add (newid);
			if (currentOldPostIds.Contains (newid))
				currentOldPostIds.Remove (newid);
		}

		public void SaveChanges ()
		{
			db.SafeRunInTransaction (() => {
				db.SafeExecute ("DELETE FROM tag_post WHERE TagId = ?", tagId);
				foreach (var id in currentActualPostIds)
					InsertTagPost (id, TagPost.StatusActual);
				foreach (var id in newPostIds)
					InsertTagPost (id, TagPost.StatusActual);
				foreach (var id in currentOldPostIds)
					InsertTagPost (id, TagPost.StatusOld);
			});
		}

		void InsertTagPost (int id, int status)
		{
			db.SafeInsert (new TagPost { TagId = tagId, PostId = id, Status = status });
		}
	}
}