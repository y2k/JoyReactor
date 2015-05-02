using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Database
{
    class Repository
    {
        protected AsyncSQLiteConnection Connection { get; } = ServiceLocator.Current.GetInstance<AsyncSQLiteConnection>();
    }
}