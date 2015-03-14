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

            InsertMain("anime", "Anime", "http://img1.joyreactor.cc/pics/avatar/tag/2851");
            InsertMain("cosplay", "Cosplay", "http://img8.joyreactor.cc/pics/avatar/tag/518");
            InsertMain("dev", "Dev", "http://img0.joyreactor.cc/pics/avatar/tag/2");
            InsertMain("android", "Android", "http://img6.joyreactor.cc/pics/avatar/tag/2596");
            InsertMain("ios", "iOS", "http://img0.joyreactor.cc/images/default_avatar.jpeg");
            InsertMain("гифки", "Гифки", "http://img6.joyreactor.cc/pics/avatar/tag/116");
            InsertMain("эротика", "Эротика", "http://img6.joyreactor.cc/pics/avatar/tag/676");
            InsertMain("песочница", "Песочница", "http://img0.joyreactor.cc/images/default_avatar.jpeg");
            InsertMain("комиксы", "Комиксы", "http://img0.joyreactor.cc/pics/avatar/tag/27");
        }

        void InsertSystem(ID id, string title)
        {
            db.Insert(new Tag { TagId = id.SerializeToString(), Title = title, Flags = Tag.FlagSystem });
        }

        void InsertMain(string tag, string title, string bestImage = null)
        {
            db.Insert(new Tag { 
                TagId = ID.Factory.NewTag(tag).SerializeToString(), 
                Title = title, 
                Flags = Tag.FlagShowInMain, 
                BestImage = bestImage 
            });
        }
    }
}