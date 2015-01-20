using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Parser.Data;
using Microsoft.Practices.ServiceLocation;
using System.Linq;
using System.Threading.Tasks;
using System;

namespace JoyReactor.Core.Model.Feed
{
    class FeedProvider : FeedService.IFeedProvider
    {
        IStorage storage;

        ID id;

        public FeedProvider(ID id)
        {
            this.id = id;
        }

        #region UpdateFirstPageAsync

        public async Task UpdateFirstPageAsync(ID id)
        {
            await storage.CreateTagIfNotExistsAsync(id);
            await storage.ClearOldLinkedTagsAsync(id);

            var parser = GetParserForTag(id);
            parser.NewTagInformation += async (sender, information) =>
                await storage.UpdateNextPageForTagAsync(id, information.NextPage);

            var organizer = new FirstPagePostSorter(id.SerializeToString());
            parser.NewPost += (sender, post) =>
            {
                var newid = SavePostToDatabase(id, post);
                organizer.AddNewPost(newid);
            };

            parser.NewLinkedTag += async (sender, e) => await SaveLinkedTag(id, e);

            parser.ExtractTag(id.Tag, id.Type, null);
            await organizer.SaveChangesAsync();

            TagCollectionModel.OnInvalidateEvent();
        }

        SiteApi GetParserForTag(ID id)
        {
            var parsers = ServiceLocator.Current.GetInstance<SiteApi[]>();
            return parsers.First(s => s.ParserId == id.Site);
        }

        int SavePostToDatabase(ID listId, ExportPost post)
        {
            var p = Convert(listId.Site, post);
            storage.SavePostToDatabaseAsync(listId, p).Wait(); // TODO: избавится от wait
            return p.Id;
        }

        async Task SaveLinkedTag(ID id, ExportLinkedTag linkedTag)
        {
            var dbTag = new TagLinkedTag
            {
                GroupName = linkedTag.group,
                Image = linkedTag.image,
                TagId = linkedTag.value,
                Title = linkedTag.name,
            };
            await storage.SaveLinkedTagAsync(id, dbTag);
        }

        Post Convert(ID.SiteParser parserId, ExportPost p)
        {
            return new Post
            {
                PostId = parserId + "-" + p.Id,
                CommentCount = p.CommentCount,
                Coub = p.Coub,
                Created = p.Created,
                Image = p.Image,
                ImageHeight = p.ImageHeight,
                ImageWidth = p.ImageWidth,
                Rating = p.Rating,
                Title = p.Title,
                UserImage = p.UserImage,
                UserName = p.UserName,
            };
        }

        #endregion

        public async Task UpdateNextPageAsync(ID id)
        {
            var parser = GetParserForTag(id);
            var organizer = new NextPagePostSorter(id.SerializeToString());
            parser.NewPost += (sender, post) =>
            {
                var newid = SavePostToDatabase(id, post);
                organizer.AddNewPost(newid);
            };
            parser.NewTagInformation += async (_, information) => await storage.UpdateNextPageForTagAsync(id, information.NextPage);
            parser.ExtractTag(id.Tag, id.Type, await storage.GetNextPageForTagAsync(id));
            await organizer.SaveChangesAsync();
        }

        internal interface IStorage
        {
            Task CreateTagIfNotExistsAsync(ID id);

            Task ClearOldLinkedTagsAsync(ID id);

            Task UpdateNextPageForTagAsync(ID id, int nextPage);

            Task SavePostToDatabaseAsync(ID id, Post post);

            Task SaveLinkedTagAsync(ID id, TagLinkedTag linkedTag);

            Task<int> GetNextPageForTagAsync(ID id);
        }
    }
}