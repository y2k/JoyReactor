using System;
using System.Threading.Tasks;
using System.Threading;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Inject;
using System.Linq;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using System.Xml.Serialization;
using System.IO;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model
{
	class ProfileModel : IProfileModel
	{
        private ISiteParser[] parsers = ServiceLocator.Current.GetInstance<ISiteParser[]>();

		#region IProfileModel implementation

		public Task LoginAsync (string username, string password)
		{
			return Task.Run(() => {
				var p = parsers.First(s => s.ParserId == ID.SiteParser.JoyReactor);
				var c = p.Login(username, password);

				if (c == null || c.Count < 1) throw new Exception();
				var pf = new Profile { Cookie = SerializeObject(c), Site = "" + ID.SiteParser.JoyReactor, Username = username };

				MainDb.Instance.SafeRunInTransaction(() => {
					ClearDatabaseFromOldData();
					MainDb.Instance.SafeInsert(pf);
				});

				GetCurrentProfileAsync().Wait();
			});
		}

		public Task LogoutAsync ()
		{
			return Task.Run (() => {
				MainDb.Instance.SafeRunInTransaction(() => {
					ClearDatabaseFromOldData();
				});
			});
		}

		public Task<ProfileInformation> GetCurrentProfileAsync ()
		{
			return Task.Run (() => {
				string un = MainDb.Instance.SafeExecuteScalar<string>("SELECT Username From profiles WHERE Site = ?", "" + ID.SiteParser.JoyReactor);
				if (un == null) return null;

				var p = parsers.First(s => s.ParserId == ID.SiteParser.JoyReactor);
				var pf = p.Profile(un);

				if (pf.ReadingTags != null) {
					lock (MainDb.Instance) {
						MainDb.Instance.RunInTransaction(() => {
							foreach (var t in pf.ReadingTags) {
								var id = MainDb.ToFlatId(ID.Factory.NewTag(t.Tag));
								int c = MainDb.Instance.ExecuteScalar<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", id);
								if (c == 0) {
									MainDb.Instance.Insert(new Tag {
										Flags = Tag.FlagWebRead | Tag.FlagShowInMain,
										TagId = id,
										Title = t.Title,
									});
								}
							}
						});
					}
				}

				return new ProfileInformation {
					Username = pf.Username,
					Rating = pf.Rating,
				};
			});
		}

		#endregion

		#region Private methods

		private static void ClearDatabaseFromOldData() {
			MainDb.Instance.SafeExecute("DELETE FROM posts");
			MainDb.Instance.SafeExecute("DELETE FROM tag_post");
			MainDb.Instance.SafeExecute("DELETE FROM tags WHERE Flags & ? != 0", Tag.FlagWebRead);
			MainDb.Instance.SafeExecute("DELETE FROM profiles");
		}

		private static string SerializeObject(IDictionary<string, string> o) 
		{
			return o.Aggregate ("", (a, s) => a + (a.Length > 0 ? ";" : "") + s.Key + "=" + s.Value);
		}

		private static IDictionary<string, string> DeserializeObject<T>(string o) 
		{
			return o.Split (';').Select (s => s.Split ('=')).ToDictionary (s => s [0], s => s [1]);
		}

		#endregion
	}
}