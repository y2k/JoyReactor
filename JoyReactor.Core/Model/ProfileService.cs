using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Parser;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    class ProfileService : IProfileService
    {
        IAuthStorage storage = new AuthRepository();
        JoyReactorProvider provider = JoyReactorProvider.Create();

        public async Task<Profile> GetMyProfile()
        {
            await provider.LoadCurrentUserProfileAsync();
            return await storage.GetCurrentProfileAsync();
        }

        public async Task Login(string username, string password)
        {
            await provider.LoginAsync(username, password);
            await provider.LoadCurrentUserProfileAsync();
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

        internal interface IAuthStorage
        {
            Task ClearDatabase();

            Task<Profile> GetCurrentProfileAsync();
        }
    }
}