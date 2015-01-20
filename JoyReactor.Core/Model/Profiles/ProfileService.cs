using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Profiles
{
    class ProfileService : IProfileService
    {
        IAuthStorage storage = new AuthStorage();

        public async Task<MyProfile> GetMyProfile()
        {
            var loader = new MyProfileLoader();
            await loader.LoadAsync();
            if (!loader.IsValid)
                throw new NotLogedException();
            return new MyProfile { UserName = loader.UserName, Rating = loader.Rating, UserImage = "" + loader.UserImage };
        }

        #region Login

        public async Task Login(string username, string password)
        {
            var cookies = await GetParser().LoginAsync(username, password);
            if (cookies == null || cookies.Count < 1)
                throw new Exception("Can't login as " + username);

            await storage.SaveCookieToDatabase(username, cookies);
            await SyncListOfMyTagsWithWeb();
        }

        SiteApi GetParser()
        {
            return ServiceLocator.Current
                .GetInstance<SiteApi[]>()
                .First(s => s.ParserId == ID.SiteParser.JoyReactor);
        }

        async Task SyncListOfMyTagsWithWeb()
        {
            await new MyProfileLoader().LoadAsync();
            await InvaliteTagList();
        }

        #endregion

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
            Task SaveCookieToDatabase(string username, IDictionary<string, string> cookies);

            Task ClearDatabase();
        }
    }
}