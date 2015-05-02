using JoyReactor.Core.Model.DTO;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Database
{
    class CreateDefaultTagsTransaction
    {
        AsyncSQLiteConnection connection;

        public CreateDefaultTagsTransaction(AsyncSQLiteConnection connection)
        {
            this.connection = connection;
        }

        public async Task Execute()
        {
            await InsertSystem(ID.Factory.New(ID.IdConst.ReactorGood), "JoyReactor");
            await InsertMain("anime", "Anime", "http://img1.joyreactor.cc/pics/avatar/tag/2851");
            await InsertMain("cosplay", "Cosplay", "http://img8.joyreactor.cc/pics/avatar/tag/518");
            await InsertMain("dev", "Dev", "http://img0.joyreactor.cc/pics/avatar/tag/2");
            await InsertMain("android", "Android", "http://img6.joyreactor.cc/pics/avatar/tag/2596");
            await InsertMain("ios", "iOS", "http://img0.joyreactor.cc/images/default_avatar.jpeg");
            await InsertMain("гифки", "Гифки", "http://img6.joyreactor.cc/pics/avatar/tag/116");
            await InsertMain("эротика", "Эротика", "http://img6.joyreactor.cc/pics/avatar/tag/676");
            await InsertMain("песочница", "Песочница", "http://img0.joyreactor.cc/images/default_avatar.jpeg");
            await InsertMain("комиксы", "Комиксы", "http://img0.joyreactor.cc/pics/avatar/tag/27");
        }

        Task InsertSystem(ID id, string title)
        {
            return connection.InsertAsync(new Tag { TagId = id.SerializeToString(), Title = title, Flags = Tag.FlagSystem });
        }

        Task InsertMain(string tag, string title, string bestImage = null)
        {
            return connection.InsertAsync(
                new Tag
                {
                    TagId = ID.Factory.NewTag(tag).SerializeToString(),
                    Title = title,
                    Flags = Tag.FlagShowInMain,
                    BestImage = bestImage
                });
        }
    }
}