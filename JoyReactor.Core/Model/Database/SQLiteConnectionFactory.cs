using Microsoft.Practices.ServiceLocation;
using PCLStorage;
using SQLite.Net;
using SQLite.Net.Interop;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    public static class SQLiteConnectionFactory
    {
        // TODO: перед релизом сбросить название базы
        const string DatabaseName = "net.itwister.joyreactor.main.2.db";

        static Lazy<AsyncSQLiteConnection> LazyInstance
            = new Lazy<AsyncSQLiteConnection>(
                  () =>
            {
                var path = PortablePath.Combine(FileSystem.Current.LocalStorage.Path, DatabaseName);
                var platform = ServiceLocator.Current.GetInstance<ISQLitePlatform>();
                var result = new AsyncSQLiteConnection(new SQLiteConnection(platform, path));
                Initialize(new InitializeTransaction(result));
                return result;
            });

        static void Initialize(InitializeTransaction result)
        {
            // TODO: убрать блокировку потока
            var semaphore = new SemaphoreSlim(0);
            Task.Run(async () =>
                {
                    await result.Execute();
                    semaphore.Release();
                });
            semaphore.Wait();
        }

        internal static AsyncSQLiteConnection Create()
        {
            return LazyInstance.Value;
        }

        public static SQLiteConnection CreateForTests(ISQLitePlatform platform)
        {
            var result = new SQLiteConnection(platform, ":memory:");
            Initialize(new InitializeTransaction(new AsyncSQLiteConnection(result)));
            return result;
        }
    }
}