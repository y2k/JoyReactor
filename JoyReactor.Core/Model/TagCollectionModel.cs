using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model
{
	public class TagCollectionModel
	{
		ISQLiteConnection connection = ServiceLocator.Current.GetInstance<ISQLiteConnection> ();

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