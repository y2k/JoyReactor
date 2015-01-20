using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Practices.ServiceLocation;
using SQLite.Net;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Model.Profiles
{
    class MyProfileLoader
    {
        public string UserName { get; set; }

        public Uri UserImage { get; set; }

        public float Rating { get; set; }

        public bool IsValid { get; set; }

        public async Task LoadAsync()
        {
            string name = await GetMyUsername();
            if (name == null)
            {
                IsValid = false;
            }
            else
            {
                var profile = await GetParser().ProfileAsync(name);
                await UpdateMyTagsFromWeb(profile.ReadingTags);

                UserName = profile.UserName;
                UserImage = profile.UserImage;
                Rating = profile.Rating;
                IsValid = true;
            }
        }

        Task<string> GetMyUsername()
        {
            return GetDB().ExecuteScalarAsync<string>(
                "SELECT Username FROM profiles WHERE Site = ?", "" + ID.SiteParser.JoyReactor);
        }

        SQLiteConnection GetDB()
        {
            return ServiceLocator.Current.GetInstance<SQLiteConnection>();
        }

        SiteApi GetParser()
        {
            return ServiceLocator.Current
				.GetInstance<SiteApi[]>()
				.First(s => s.ParserId == ID.SiteParser.JoyReactor);
        }

        Task UpdateMyTagsFromWeb(List<ProfileExport.TagExport> tags)
        {
            return GetDB().RunInTransactionAsync(() =>
                {
                    foreach (var t in tags)
                    {
                        var id = ID.Factory.NewTag(t.Tag).SerializeToString();
                        int c = GetDB().ExecuteScalar<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id);
                        if (c == 0)
                        {
                            GetDB().Insert(new Tag
                                {
                                    Flags = Tag.FlagWebRead | Tag.FlagShowInMain,
                                    TagId = id,
                                    Title = t.Title,
                                });
                        }
                    }
                });
        }
    }
}