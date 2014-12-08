using System.Collections.ObjectModel;
using System.Collections.Generic;

namespace JoyReactor.Core.VideModels
{
	public static class ObservableCollectionExtensions
	{
		public static void ReplaceAll <T> (this ObservableCollection<T> collection, IEnumerable<T> newItems)
		{
			collection.Clear ();
			foreach (var s in newItems)
				collection.Add (s);
		}
	}
}