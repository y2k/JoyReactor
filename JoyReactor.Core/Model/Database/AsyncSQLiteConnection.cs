using SQLite.Net;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class AsyncSQLiteConnection
    {
        private SQLiteConnection connection;

        public AsyncSQLiteConnection(SQLiteConnection connection)
        {
            this.connection = connection;
        }

        internal Task CreateTableAsync<T>()
        {
            connection.CreateTable<T>();

            throw new NotImplementedException();
        }

        internal Task<List<T>> QueryAsync<T>(string query, params object[] args) where T : class
        {
            connection.Query<T>(query, args);

            throw new NotImplementedException();
        }

        internal Task<int> InsertAsync(object item)
        {
            connection.Insert(item);

            throw new NotImplementedException();
        }

        internal Task ExecuteAsync(string query, params object[] args)
        {
            connection.Execute(query, args);

            throw new NotImplementedException();
        }

        internal Task<T> ExecuteScalarAsync<T>(string query, params object[] args)
        {
            connection.ExecuteScalar<T>(query, args);

            throw new NotImplementedException();
        }

        internal Task UpdateAsync(object item)
        {
            connection.Update(item);

            throw new NotImplementedException();
        }

        internal Task InsertAllAsync<T>(ICollection<T> items)
        {
            connection.InsertAll(items);

            throw new NotImplementedException();
        }
    }
}