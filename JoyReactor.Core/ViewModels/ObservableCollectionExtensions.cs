using System.Collections.ObjectModel;
using System.Collections.Generic;

namespace JoyReactor.Core.ViewModels
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
    }
}