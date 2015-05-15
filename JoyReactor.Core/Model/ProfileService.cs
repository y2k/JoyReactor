using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using System.Threading.Tasks;
using System;
using System.Linq;
using System.Collections.Generic;

namespace JoyReactor.Core.Model
{
    class ProfileService : IProfileService, ProfileProvider.Storage
    {
        IAuthStorage storage = new AuthRepository();
        JoyReactorProvider provider = JoyReactorProvider.Create();

        public async Task<Profile> GetMyProfile()
        {
            await provider.LoadCurrentUserProfileAsync(this);
            return await storage.GetCurrentProfileAsync();
        }

        public async Task Login(string username, string password)
        {
            await provider.LoginAsync(username, password);
            await provider.LoadCurrentUserProfileAsync(this);
            await InvaliteTagList();
        }

        public async Task Logout()
        {
            await storage.ClearDatabase();
            await InvaliteTagList();
        }

        Task InvaliteTagList()
        {
            return Task.Run(() => TagCollectionModel.OnInvalidateEvent());
        }

        public async Task ReplaceCurrentUserReadingTagsAsync(IEnumerable<Tag> readingTags)
        {
            foreach (var t in readingTags)
            {
                t.Flags = Tag.FlagWebRead | Tag.FlagShowInMain;
                t.TagId = ID.Factory.NewTag(t.Title.ToLower()).SerializeToString();

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