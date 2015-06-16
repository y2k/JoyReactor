using Microsoft.Practices.ServiceLocation;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Database
{
    class Repository<T>
    {
        protected AsyncSQLiteConnection Connection { get; } = ServiceLocator.Current.GetInstance<AsyncSQLiteConnection>();

        internal Task InsertAsync(T value) {
            return Connection.InsertAsync(value);
        }

        internal Task UpdateAsync(T value) {
            return Connection.UpdateAsync(value);
        }

        internal Task InsertAllAsync(ICollection<T> items)
        {
            return Connection.InsertAllAsync(items);
        }
    }
}