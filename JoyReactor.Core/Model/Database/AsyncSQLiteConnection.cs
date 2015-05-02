using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class AsyncSQLiteConnection
    {
        SemaphoreSlim semaphore = new SemaphoreSlim(1);
        SQLiteConnection connection;

        public AsyncSQLiteConnection(SQLiteConnection connection)
        {
            this.connection = connection;
        }

        internal Task CreateTableAsync<T>()
        {
            return Execute(() => connection.CreateTable<T>());
        }

        internal Task<List<T>> QueryAsync<T>(string query, params object[] args) where T : class
        {
            return Execute(() => connection.Query<T>(query, args));
        }

        internal Task<int> InsertAsync(object item)
        {
            return Execute(() => connection.Insert(item));
        }

        internal Task ExecuteAsync(string query, params object[] args)
        {
            return Execute(() => connection.Execute(query, args));
        }

        internal Task<T> ExecuteScalarAsync<T>(string query, params object[] args)
        {
            return Execute(() => connection.ExecuteScalar<T>(query, args));
        }

        internal Task UpdateAsync(object item)
        {
            return Execute(() => connection.Update(item));
        }

        internal Task InsertAllAsync<T>(ICollection<T> items)
        {
            return Execute(() => connection.InsertAll(items));
        }

        async Task<T> Execute<T>(Func<T> callback)
        {
            await semaphore.WaitAsync();
            try
            {
                return await Task.Run(callback);
            }
            finally
            {
                semaphore.Release();
            }
        }
    }
}