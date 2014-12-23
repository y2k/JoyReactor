using JoyReactor.Core.Model.DTO;
using Microsoft.Practices.ServiceLocation;
using PCLStorage;
using SQLite.Net;
using SQLite.Net.Interop;

namespace JoyReactor.Core.Model.Database
{
    public class MainDb
    {
        const string DatabaseName = "net.itwister.joyreactor.main.db";
        const int DatabaseVersion = 1;

        static volatile SQLiteConnection instance;
        static object syncRoot = new object();

        public static SQLiteConnection Instance
        {
            get
            {
                if (instance == null)
                {
                    lock (syncRoot)
                    {
                        if (instance == null)
                        {
                            var path = PortablePath.Combine(FileSystem.Current.LocalStorage.Path, DatabaseName);
                            var platform = ServiceLocator.Current.GetInstance<ISQLitePlatform>();
                            instance = new SQLiteConnection(platform, path);
                            InitializeDatabase(instance);
                        }
                    }
                }

                return instance;
            }
        }

        public static string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }

        public static void InitializeDatabase(SQLiteConnection db)
        {
            int ver = GetUserVesion(db);
            if (ver == 0)
                db.RunInTransaction(() =>
                {
                    OnCreate(db);
                    SetUserVersion(db, DatabaseVersion);
                });
            else if (ver < DatabaseVersion)
                db.RunInTransaction(() =>
                {
                    OnUpdate(ver, DatabaseVersion);
                    SetUserVersion(db, DatabaseVersion);
                });
        }

        private static void OnCreate(SQLiteConnection db)
        {
            db.CreateTable<Post>();
            db.CreateTable<Profile>();
            db.CreateTable<Tag>();
            db.CreateTable<TagPost>();
            db.CreateTable<Profile>();
            db.CreateTable<TagLinkedTag>();
            db.CreateTable<Comment>();
            db.CreateTable<Attachment>();
            db.CreateTable<CommentLink>();

            new CreateDefaultTagsTransaction(db).Execute();
        }

        protected static void OnUpdate(int oldVersion, int newVersion)
        {
            // Reserverd
        }

        static void SetUserVersion(SQLiteConnection db, int version)
        {
            db.Execute("PRAGMA user_version = " + version);
        }

        static int GetUserVesion(SQLiteConnection db)
        {
            return db.ExecuteScalar<int>("PRAGMA user_version");
        }
    }
}