using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class InitializeTransaction
    {
        const int DatabaseVersion = 1;

        AsyncSQLiteConnection connection;

        internal InitializeTransaction(AsyncSQLiteConnection connection)
        {
            this.connection = connection;
        }

        internal async Task Execute()
        {
            int ver = await GetUserVesion(connection);
            if (ver == 0)
            {
                await OnCreate(connection);
                await SetUserVersion(connection, DatabaseVersion);
            }
            else if (ver < DatabaseVersion)
            {
                // XXX: OnUpdate
                await SetUserVersion(connection, DatabaseVersion);
            }
        }

        async Task OnCreate(AsyncSQLiteConnection db)
        {
            await new CreateTablesTransaction(db).Execute();
            await new CreateDefaultTagsTransaction(db).Execute();
        }

        Task SetUserVersion(AsyncSQLiteConnection db, int version)
        {
            return db.ExecuteAsync("PRAGMA user_version = " + version);
        }

        Task<int> GetUserVesion(AsyncSQLiteConnection db)
        {
            return db.ExecuteScalarAsync<int>("PRAGMA user_version");
        }
    }
}
