using System;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
	public interface ITagCollectionModel
	{
		Task<List<Tag>> GetMainSubscriptionsAsync();

		Task<List<TagLinkedTag>> GetTagLinkedTagsAsync(ID tagId);
	}
}