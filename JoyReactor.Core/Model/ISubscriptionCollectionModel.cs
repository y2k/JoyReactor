using System;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
	public interface ISubscriptionCollectionModel
	{
		Task<List<Tag>> GetMainSubscriptionsAsync();
	}
}