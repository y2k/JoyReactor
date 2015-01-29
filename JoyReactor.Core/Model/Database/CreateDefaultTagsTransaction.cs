using JoyReactor.Core.Model.DTO;
using SQLite.Net;

namespace JoyReactor.Core.Model.Database
{
    class CreateDefaultTagsTransaction
    {
        SQLiteConnection db;

        public CreateDefaultTagsTransaction(SQLiteConnection db)
        {
            this.db = db;
        }

        public void Execute()
        {
            InsertSystem(ID.Factory.New(ID.IdConst.ReactorGood), "JoyReactor");

            InsertMain(ID.Factory.NewTag("anime"), "Anime", "http://img1.joyreactor.cc/pics/avatar/tag/2851");
            InsertMain(ID.Factory.NewTag("cosplay"), "Cosplay", "http://img8.joyreactor.cc/pics/avatar/tag/518");
            InsertMain(ID.Factory.NewTag("dev"), "Dev", "http://img0.joyreactor.cc/pics/avatar/tag/2");
            InsertMain(ID.Factory.NewTag("android"), "Android", "http://img6.joyreactor.cc/pics/avatar/tag/2596");
            InsertMain(ID.Factory.NewTag("ios"), "iOS", "http://img0.joyreactor.cc/images/default_avatar.jpeg");
            InsertMain(ID.Factory.NewTag("гифки"), "Гифки", "http://img6.joyreactor.cc/pics/avatar/tag/116");
            InsertMain(ID.Factory.NewTag("эротика"), "Эротика", "http://img6.joyreactor.cc/pics/avatar/tag/676");
            InsertMain(ID.Factory.NewTag("песочница"), "Песочница", "http://img0.joyreactor.cc/images/default_avatar.jpeg");
            InsertMain(ID.Factory.NewTag("комиксы"), "Комиксы", "http://img0.joyreactor.cc/pics/avatar/tag/27");
        }

        void InsertSystem(ID id, string title)
        {
            db.Insert(new Tag { TagId = id.SerializeToString(), Title = title, Flags = Tag.FlagSystem });
        }

        void InsertMain(ID id, string title, string bestImage = null)
        {
            db.Insert(new Tag { TagId = id.SerializeToString(), Title = title, Flags = Tag.FlagShowInMain, BestImage = bestImage });
        }
    }
}