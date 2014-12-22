using System;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;
using SQLite.Net;
using SQLite.Net.Interop;
using SQLite.Net.Platform.Generic;
using SQLite.Net.Platform.Win32;

namespace JoyReactor.Core.Tests.Inner
{
	public class MockSQLiteConnection
	{
		static readonly Dictionary<PlatformID, ISQLitePlatform> Platforms = new Dictionary<PlatformID, ISQLitePlatform> {
			[PlatformID.Win32Windows] = new SQLitePlatformWin32(),
			[PlatformID.Unix] = new SQLitePlatformGeneric(),
		};

		public static SQLiteConnection Create ()
		{
			var platform = Platforms [Environment.OSVersion.Platform];
			var db = new SQLiteConnection (platform, ":memory:");
			db.CreateTable<Post> ();
			db.CreateTable<Tag> ();
			db.CreateTable<TagPost> ();
			db.CreateTable<Profile> ();
			db.CreateTable<TagLinkedTag> ();
			db.CreateTable<Comment> ();
			db.CreateTable<Attachment> ();
			db.CreateTable<CommentLink> ();
			return db;
		}
	}
}