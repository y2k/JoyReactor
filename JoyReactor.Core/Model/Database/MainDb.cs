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

		public static SQLiteConnection Instance {
			get {
				if (instance == null) {
					lock (syncRoot) {
						if (instance == null) {
                            var path = PortablePath.Combine(FileSystem.Current.LocalStorage.Path, DatabaseName);
                            var platform = ServiceLocator.Current.GetInstance<ISQLitePlatform>();
                            instance = new SQLiteConnection(platform, path);
							InitializeDatabase (instance);
						}
					}
				}

				return instance;
			}
		}

		public static string ToFlatId (ID id)
		{
			return id.Site + "-" + id.Type + "-" + id.Tag;
		}

		public static void InitializeDatabase (SQLiteConnection db)
		{
			int ver = GetUserVesion (db);
			if (ver == 0)
				db.RunInTransaction (() => {
					OnCreate (db);
					SetUserVersion (db, DatabaseVersion);
				});
			else if (ver < DatabaseVersion)
				db.RunInTransaction (() => {
					OnUpdate (ver, DatabaseVersion);
					SetUserVersion (db, DatabaseVersion);
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

            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.New(ID.IdConst.ReactorGood)),
                Title = "JoyReactor",
                Flags = Tag.FlagSystem
            });

#if DEBUG
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan2, "b")),
                Title = "2ch / b",
                Flags = Tag.FlagShowInMain
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan4, "b")),
                Title = "4chan / b",
                Flags = Tag.FlagShowInMain
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.New(ID.SiteParser.Chan7, "b")),
                Title = "7chan / b",
                Flags = Tag.FlagShowInMain
            });
#endif

            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("anime")),
                Title = "Anime",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img1.joyreactor.cc/pics/avatar/tag/2851"
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("cosplay")),
                Title = "Cosplay",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img8.joyreactor.cc/pics/avatar/tag/518"
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("android")),
                Title = "Android",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/2596"
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("гифки")),
                Title = "Гифки",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/116"
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("эротика")),
                Title = "Эротика",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/676"
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("песочница")),
                Title = "Песочница",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img0.joyreactor.cc/images/default_avatar.jpeg"
            });
            db.Insert(new Tag
            {
                TagId = ToFlatId(ID.Factory.NewTag("комиксы")),
                Title = "Комиксы",
                Flags = Tag.FlagShowInMain,
                BestImage = "http://img0.joyreactor.cc/pics/avatar/tag/27"
            });
        }

        protected static void OnUpdate(int oldVersion, int newVersion)
        {
            // Reserverd
        }

        static void SetUserVersion (SQLiteConnection db, int version)
		{
			db.Execute ("PRAGMA user_version = " + version);
		}

		static int GetUserVesion (SQLiteConnection db)
		{
			return db.ExecuteScalar<int> ("PRAGMA user_version");
		}
	}
}