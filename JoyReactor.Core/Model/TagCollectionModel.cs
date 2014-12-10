using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    public class TagCollectionModel
	{
		SQLiteConnection connection = ServiceLocator.Current.GetInstance<SQLiteConnection> ();

		public Task<List<Tag>> GetMainSubscriptionsAsync ()
		{
			return Task.Run (() => {
				return connection.SafeQuery<Tag> ("SELECT * FROM tags WHERE Flags & ? != 0", Tag.FlagShowInMain);
			});
		}

		public Task<List<TagLinkedTag>> GetTagLinkedTagsAsync (ID tagId)
		{
			return Task.Run (() => {
				return connection.SafeQuery<TagLinkedTag> (
					"SELECT * FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)", 
					MainDb.ToFlatId (tagId));
			});
		}
	}
}