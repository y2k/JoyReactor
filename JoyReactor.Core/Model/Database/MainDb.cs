using System;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.DTO;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;

namespace JoyReactor.Core.Model.Database
{
    public class MainDb
    {
        private const string DatabaseName = "net.itwister.joyreactor.main.db";
        private const int DatabaseVersion = 1;

        private static volatile ISQLiteConnection instance;
        private static object syncRoot = new Object();

        public static ISQLiteConnection Instance
        {
            get
            {
                if (instance == null)
                {
                    lock (syncRoot)
                    {
                        if (instance == null)
                        {
                            //var plat = InjectService.Instance.Get<ISQLitePlatfromGetter> ();
                            //instance = new MainDb (plat.GetPlatform(), plat.CreatePath(DatabaseName));

                            var f = InjectService.Instance.Get<ISQLiteConnectionFactory>();
                            instance = f.Create(DatabaseName);
                            InitializeDatabase(instance);
                        }
                    }
                }

                return instance;
            }
        }

        private static void InitializeDatabase(ISQLiteConnection db)
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

        private static void OnCreate(ISQLiteConnection db)
        {
            db.CreateTable<Post>();
            db.CreateTable<Tag>();
            db.CreateTable<TagPost>();
            db.CreateTable<Profile>();
            db.CreateTable<TagLinkedTag>();
            db.CreateTable<Comment>();

            db.Insert(new Tag { TagId = ToFlatId(ID.REACTOR_GOOD), Title = "JoyReactor", Flags = Tag.FlagSystem });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("anime")), Title = "Anime", Flags = Tag.FlagShowInMain, BestImage = "http://img1.joyreactor.cc/pics/avatar/tag/2851" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("cosplay")), Title = "Cosplay", Flags = Tag.FlagShowInMain, BestImage = "http://img8.joyreactor.cc/pics/avatar/tag/518" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("android")), Title = "Android", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/2596" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("гифки")), Title = "Гифки", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/116" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("эротика")), Title = "Эротика", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/676" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("песочница")), Title = "Песочница", Flags = Tag.FlagShowInMain, BestImage = "http://img0.joyreactor.cc/images/default_avatar.jpeg" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.Reactor("комиксы")), Title = "Комиксы", Flags = Tag.FlagShowInMain, BestImage = "http://img0.joyreactor.cc/pics/avatar/tag/27" });
        }

        private static void OnUpdate(int oldVersion, int newVersion)
        {
            // Reserverd
        }

        public static string ToFlatId(ID id)
        {
            return id.Site + "-" + id.Type + "-" + id.Tag;
        }

        #region Private methods

        private static void SetUserVersion(ISQLiteConnection db, int version)
        {
            db.Execute("PRAGMA user_version = " + version);
        }

        private static int GetUserVesion(ISQLiteConnection db)
        {
            return db.ExecuteScalar<int>("PRAGMA user_version");
        }

        #endregion
    }
}