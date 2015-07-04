using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Database
{
    class Repository<T>
    {
        protected readonly AsyncSQLiteConnection Connection = 
            ServiceLocator.Current.GetInstance<AsyncSQLiteConnection>();

        internal Task InsertAsync(T value)
        {
            return Connection.InsertAsync(value);
        }

        internal Task UpdateAsync(T value)
        {
            return Connection.UpdateAsync(value);
        }

        internal Task InsertAllAsync(ICollection<T> items)
        {
            return Connection.InsertAllAsync(items);
        }
    }
}