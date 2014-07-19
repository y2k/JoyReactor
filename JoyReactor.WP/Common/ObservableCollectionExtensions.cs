using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.Common
{
    internal static class ObservableCollectionExtensions
    {
        public static void AddRange<T>(this ObservableCollection<T> instance, IEnumerable<T> items)
        {
            foreach (var s in items)
            {
                instance.Add(s);
            }
        }
        public static void Replace<T>(this ObservableCollection<T> instance, IEnumerable<T> items)
        {
            instance.Clear();
            foreach (var s in items)
            {
                instance.Add(s);
            }
        }
    }
}