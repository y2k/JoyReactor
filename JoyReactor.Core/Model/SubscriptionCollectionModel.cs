using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;

namespace JoyReactor.Core.Model
{
	public class SubscriptionCollectionModel : ISubscriptionCollectionModel
	{
		#region ISubscriptionCollectionModel implementation

		public Task<List<Tag>> GetMainSubscriptionsAsync ()
		{
			return Task<List<Tag>>.Run (() => MainDb.Instance.Query<Tag>("SELECT * FROM tags"));
		}

		#endregion
	}
}