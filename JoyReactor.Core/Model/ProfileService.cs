using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Model
{
    class ProfileService : ProfileRequest.Storage
    {
        IAuthStorage storage = new AuthRepository();

        public async Task<Profile> GetMyProfile()
        {
            await new ProfileRequest(this).ComputeAsync();
            return await storage.GetCurrentProfileAsync();
        }

        public async Task Login(string username, string password)
        {
            await new LoginRequest(username, password).ComputeAsync();
            await new ProfileRequest(this).ComputeAsync();
            await InvaliteTagList();
        }

        public async Task Logout()
        {
            await storage.ClearDatabase();
            await InvaliteTagList();
        }

        Task InvaliteTagList()
        {
            return Task.Run(() => TagCollectionModel.InvalidateTagCollection());
        }

        public async Task ReplaceCurrentUserReadingTagsAsync(IEnumerable<Tag> readingTags)
        {
            foreach (var t in readingTags)
            {
                t.Flags = Tag.FlagWebRead | Tag.FlagShowInMain;

                int count = await new TagRepository().CountAsync(t.TagId);
                if (count == 0)
                    await new TagRepository().InsertAsync(t);
            }
        }

        public async Task SaveNewOrUpdateProfileAsync(Profile profile)
        {
            var repository = new ProfileRepository();
            profile.Cookie = (await repository.GetAllAsync()).FirstOrDefault()?.Cookie;
            await repository.RemoveAllAsync();
            await repository.InsertAsync(profile);
        }

        internal interface IAuthStorage
        {
            Task ClearDatabase();

            Task<Profile> GetCurrentProfileAsync();
        }
    }
}