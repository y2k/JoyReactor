using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;

namespace JoyReactor.Core.Model
{
	class TagCollectionModel : ITagCollectionModel
	{
		#region ISubscriptionCollectionModel implementation

		public Task<List<Tag>> GetMainSubscriptionsAsync ()
		{
			return Task.Run (() => {
				lock (MainDb.Instance) {
					return MainDb.Instance.Query<Tag>("SELECT * FROM tags WHERE Flags & ? != 0", Tag.FlagShowInMain);
				}
			});
		}

		public Task<List<TagLinkedTag>> GetTagLinkedTagsAsync (ID tagId)
		{
			return Task<List<TagLinkedTag>>.Run (() => {
				lock (MainDb.Instance) {
					return MainDb.Instance.Query<TagLinkedTag>(
						"SELECT * FROM tag_linked_tags WHERE ParentTagId IN (SELECT Id FROM tags WHERE TagId = ?)", 
						MainDb.ToFlatId(tagId));
				}
			});
		}

		#endregion
	}
}