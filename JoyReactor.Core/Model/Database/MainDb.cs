using System;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.DTO;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Database
{
    internal class MainDb
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
                            var f = ServiceLocator.Current.GetInstance<ISQLiteConnectionFactory>();
                            instance = f.Create(DatabaseName);
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

        protected static void OnCreate(ISQLiteConnection db)
        {
            db.CreateTable<Post>();
            db.CreateTable<Tag>();
            db.CreateTable<TagPost>();
            db.CreateTable<Profile>();
            db.CreateTable<TagLinkedTag>();
            db.CreateTable<Comment>();
            db.CreateTable<CommentAttachment>();
			db.CreateTable<CommentLink> ();

            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.IdConst.ReactorGood)), Title = "JoyReactor", Flags = Tag.FlagSystem });

            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan2, "b")), Title = "2ch / b", Flags = Tag.FlagShowInMain });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan2, "media")), Title = "2ch / gif", Flags = Tag.FlagShowInMain });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan4, "b")), Title = "4chan / b", Flags = Tag.FlagShowInMain });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan4, "gif")), Title = "4chan / gif", Flags = Tag.FlagShowInMain });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan4, "wfg")), Title = "4chan / SFW gif", Flags = Tag.FlagShowInMain });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan7, "b")), Title = "7chan / b", Flags = Tag.FlagShowInMain });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan7, "gif")), Title = "7chan / gif", Flags = Tag.FlagShowInMain });

            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("anime")), Title = "Anime", Flags = Tag.FlagShowInMain, BestImage = "http://img1.joyreactor.cc/pics/avatar/tag/2851" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("cosplay")), Title = "Cosplay", Flags = Tag.FlagShowInMain, BestImage = "http://img8.joyreactor.cc/pics/avatar/tag/518" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("android")), Title = "Android", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/2596" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("гифки")), Title = "Гифки", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/116" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("эротика")), Title = "Эротика", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/676" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("песочница")), Title = "Песочница", Flags = Tag.FlagShowInMain, BestImage = "http://img0.joyreactor.cc/images/default_avatar.jpeg" });
            db.Insert(new Tag { TagId = ToFlatId(ID.Factory.NewTag("комиксы")), Title = "Комиксы", Flags = Tag.FlagShowInMain, BestImage = "http://img0.joyreactor.cc/pics/avatar/tag/27" });
        }

        protected static void OnUpdate(int oldVersion, int newVersion)
        {
            // Reserverd
        }

        #region Private methods

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