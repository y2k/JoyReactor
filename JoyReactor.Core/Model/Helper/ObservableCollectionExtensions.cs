using System.Collections.ObjectModel;
using System.Collections.Generic;
using System.Linq;

namespace JoyReactor.Core.Model.Helper
{
    public static class ObservableCollectionExtensions
    {
        public static void ReplaceAll<T>(this ObservableCollection<T> collection, IEnumerable<T> newItems)
        {
            collection.Clear();
            foreach (var s in newItems)
                collection.Add(s);
        }

        public static void ReplaceAll<T>(this ObservableCollection<T> collection, int from, IEnumerable<T> newItems)
        {
            for (int i = collection.Count - 1; i >= from; i--)
                collection.RemoveAt(i);
            foreach (var s in newItems)
                collection.Add(s);
        }

        public static void AddRange<T>(this ObservableCollection<T> collection, IEnumerable<T> newItems)
        {
            foreach (var s in newItems)
                collection.Add(s);
        }

        public static void ReplaceAt<T>(this ObservableCollection<T> collection, int position, T newItem)
        {
            if (position >= collection.Count)
                collection.AddRange(Enumerable.Range(0, position).Select(s => default(T)));
            collection.Insert(position, newItem);
        }
    }
}