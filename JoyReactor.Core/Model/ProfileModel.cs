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

namespace JoyReactor.Core.Model
{
	public class ProfileModel : IProfileModel
	{
		private ISiteParser[] parsers = InjectService.Instance.Get<ISiteParser[]>();

		#region IProfileModel implementation

		public Task LoginAsync (string username, string password)
		{
			return Task.Run(() => {
				var p = parsers.First(s => s.ParserId == ID.SiteParser.JoyReactor);
				var c = p.Login(username, password);

				if (c == null || c.Count < 1) throw new Exception();

				var pf = new Profile { Cookie = SerializeObject(c), Site = "" + ID.SiteParser.JoyReactor, Username = username };
				MainDb.Instance.InsertOrReplace(pf);
			});
		}

		public Task LogoutAsync ()
		{
			throw new NotImplementedException ();
		}

		public Task<ProfileInformation> GetCurrentProfileAsync ()
		{
			return Task.Run (() => {
				ThreadHelper.Sleep(2000);
//				return new ProfileInformation();
				return (ProfileInformation)null;
			});
		}

		#endregion

		#region Private methods

		private static string SerializeObject(object toSerialize) {
			var xmlSerializer = new XmlSerializer(toSerialize.GetType());
			var textWriter = new StringWriter();

			xmlSerializer.Serialize(textWriter, toSerialize);
			return textWriter.ToString();
		}

		private static T DeserializeObject<T>(string serializedObject) {
			var ser = new XmlSerializer (typeof(T));
			var r = new StringReader (serializedObject);

			return (T)ser.Deserialize (r);
		}

		#endregion
	}
}