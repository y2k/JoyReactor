using JoyReactor.Core.Model.DTO;
using SQLite.Net;

namespace JoyReactor.Core.Tests.Inner
{
    public class MockSQLiteConnection
    {
        public static SQLiteConnection Create()
        {
            var db = new SQLiteConnection(new SQLite.Net.Platform.Win32.SQLitePlatformWin32(), ":memory:");
            db.CreateTable<Post>();
            db.CreateTable<Tag>();
            db.CreateTable<TagPost>();
            db.CreateTable<Profile>();
            db.CreateTable<TagLinkedTag>();
            db.CreateTable<Comment>();
            db.CreateTable<CommentAttachment>();
            db.CreateTable<CommentLink>();
            return db;
        }
    }
}