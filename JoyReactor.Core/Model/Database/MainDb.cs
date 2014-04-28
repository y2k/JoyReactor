using System;
using SQLite.Net;
using SQLite.Net.Interop;
using Ninject;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.DTO;

namespace JoyReactor.Core.Model.Database
{
	public class MainDb : SQLiteConnection
	{
		private const string DatabaseName = "net.itwister.joyreactor.main.db";
		private const int DatabaseVersion = 1;

		private static volatile MainDb instance;
		private static object syncRoot = new Object();

		public static SQLiteConnection Instance
		{
			get 
			{
				if (instance == null) {
					lock (syncRoot) {
						if (instance == null) {
							var plat = InjectService.Instance.Get<ISQLitePlatfromGetter> ();
							instance = new MainDb (plat.GetPlatform(), plat.CreatePath(DatabaseName));
						}
					}
				}
				return instance;
			}
		}

		private MainDb (ISQLitePlatform platform, string path) : base(platform, path)
		{
			int ver = GetUserVesion ();
			if (ver == 0)
				RunInTransaction (() => {
					OnCreate ();
					SetUserVersion (DatabaseVersion);
				});
			else if (ver < DatabaseVersion)
				RunInTransaction (() => {
					OnUpdate(ver, DatabaseVersion);
					SetUserVersion (DatabaseVersion);
				});
		}

		protected void OnCreate()
		{
			CreateTable<Post> ();
			CreateTable<Tag> ();
			CreateTable<TagPost> ();

			Insert (new Tag { TagId = ToFlatId(ID.REACTOR_GOOD), Title = "JoyReactor", Flags = Tag.FlagSystem });
			Insert (new Tag { TagId = ToFlatId(ID.Factory.Reactor("anime")), Title = "Anime", Flags = Tag.FlagShowInMain, BestImage = "http://img1.joyreactor.cc/pics/avatar/tag/2851" });
			Insert (new Tag { TagId = ToFlatId(ID.Factory.Reactor("cosplay")), Title = "Cosplay", Flags = Tag.FlagShowInMain, BestImage = "http://img8.joyreactor.cc/pics/avatar/tag/518" });
			Insert (new Tag { TagId = ToFlatId(ID.Factory.Reactor("android")), Title = "Android", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/2596" });
			Insert (new Tag { TagId = ToFlatId(ID.Factory.Reactor("гифки")), Title = "Гифки", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/116" });
			Insert (new Tag { TagId = ToFlatId(ID.Factory.Reactor("эротика")), Title = "Эротика", Flags = Tag.FlagShowInMain, BestImage = "http://img6.joyreactor.cc/pics/avatar/tag/676" });
			Insert (new Tag { TagId = ToFlatId(ID.Factory.Reactor("песочница")), Title = "Песочница", Flags = Tag.FlagShowInMain, BestImage = "http://img0.joyreactor.cc/images/default_avatar.jpeg" });
		}

		protected void OnUpdate(int oldVersion, int newVersion)
		{
			// Reserverd
		}

		public static string ToFlatId (ID id)
		{
			return id.Site + "-" + id.Type + "-" + id.Tag;
		}

		#region Private methods

		private void SetUserVersion (int version)
		{
			Execute ("PRAGMA user_version = " + version);
		}

		private int GetUserVesion() 
		{
			return ExecuteScalar<int> ("PRAGMA user_version");
		}

		#endregion
	}
}