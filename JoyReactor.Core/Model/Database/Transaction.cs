using System;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;

namespace JoyReactor.Core.Model.Database
{
    public class Transaction : IDisposable
    {
        SQLiteConnection connection = ServiceLocator.Current.GetInstance<SQLiteConnection>();

        public Task Run(Func<Task> callback)
        {
            return callback();
        }

        public Task<Transaction> BeginAsync()
        {
            throw new NotImplementedException();
        }

        public void Dispose()
        {
            throw new NotImplementedException();
        }
    }
}