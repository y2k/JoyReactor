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
            throw new NotImplementedException();
        }

        internal Task<T> QueryFirstAsync<T>(string sql, params object[] args)
        {
            throw new NotImplementedException();
        }

        internal Task<List<T>> QueryAsync<T>(string sql, params object[] args)
        {
            throw new NotImplementedException();
        }

        internal Task<int> InsertAsync(object item)
        {
            throw new NotImplementedException();
        }

        internal Task ExecuteAsync(string sql, params object[] args)
        {
            throw new NotImplementedException();
        }

        internal Task<T> ExecuteScalarAsync<T>(string sql, params object[] args)
        {
            throw new NotImplementedException();
        }

        internal Task UpdateAsync(object item)
        {
            throw new NotImplementedException();
        }

        internal Task InsertAllAsync<T>(ICollection<T> links)
        {
            throw new NotImplementedException();
        }
    }
}