using Microsoft.Practices.ServiceLocation;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class Repository
    {
        protected AsyncSQLiteConnection Connection { get; } = ServiceLocator.Current.GetInstance<AsyncSQLiteConnection>();

        internal Task InsertAsync(object value) {
            return Connection.InsertAsync(value);
        }

        internal Task UpdateAsync(object value) {
            return Connection.UpdateAsync(value);
        }
    }
}