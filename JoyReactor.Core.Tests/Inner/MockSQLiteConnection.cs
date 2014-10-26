using System;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System.Collections.Generic;
using System.Linq;
using JoyReactor.Core.Model.DTO;
using NUnit.Framework;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.Wpf;

namespace JoyReactor.Core.Tests.Inner
{
	public class MockSQLiteConnection
	{
		public static ISQLiteConnection Create ()
		{
			var db = new MvxWpfSqLiteConnectionFactory ().CreateInMemory ();
			db.CreateTable<Post> ();
			db.CreateTable<Tag> ();
			db.CreateTable<TagPost> ();
			db.CreateTable<Profile> ();
			db.CreateTable<TagLinkedTag> ();
			db.CreateTable<Comment> ();
			db.CreateTable<CommentAttachment> ();
			db.CreateTable<CommentLink> ();
			return db;
		}
	}
}