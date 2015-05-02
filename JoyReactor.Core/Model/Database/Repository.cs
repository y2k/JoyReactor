using SQLite.Net;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Database
{
    public class Repository
    {
        protected SQLiteConnection Connection { get; } = ServiceLocator.Current.GetInstance<SQLiteConnection>();
    }
}