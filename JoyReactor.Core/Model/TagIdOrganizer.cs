using System;
using System.Linq;
using System.Collections.Generic;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model
{
	public class TagIdOrganizer
	{
		List<int> newPostIds = new List<int> ();
		List<int> oldPostIds;
		ISQLiteConnection db;
		int tagId;

		public TagIdOrganizer (ISQLiteConnection db, string tagId)
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
			oldPostIds = db
				.SafeQuery<TagPost> ("SELECT PostId FROM tag_post WHERE TagId = ?", tagId)
				.Select (s => s.PostId)
				.ToList ();
		}

		public void AddNewPost (int newid)
		{
			if (!oldPostIds.Contains (newid))
				newPostIds.Add (newid);
		}

		public void SaveChanges ()
		{
			db.SafeRunInTransaction (() => {
				db.SafeExecute ("DELETE FROM tag_post WHERE TagId = ?", tagId);

				if (IsFirstExecution) {
					foreach (var id in newPostIds)
						db.SafeInsert (new TagPost { 
							TagId = tagId, 
							PostId = id,
							Status = TagPost.StatusComplete
						});
				} else {
					foreach (var id in newPostIds)
						db.SafeInsert (new TagPost { 
							TagId = tagId, 
							PostId = id,
							Status = TagPost.StatusNew
						});
					foreach (var id in oldPostIds)
						db.SafeInsert (new TagPost {
							TagId = tagId,
							PostId = id, 
							Status = TagPost.StatusComplete
						});
				}
			});
		}

		bool IsFirstExecution {
			get { return oldPostIds.Count <= 0; }
		}
	}
}