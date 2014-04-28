using System;
using System.Threading.Tasks;
using System.Threading;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Core.Model
{
	public class ProfileModel : IProfileModel
	{
		public ProfileModel ()
		{
		}

		#region IProfileModel implementation

		public Task LoginAsync (string username, string password)
		{
			return Task.Run(() => {



			});
		}

		public Task LogoutAsync ()
		{
			throw new NotImplementedException ();
		}

		public Task<ProfileInformation> GetCurrentProfileAsync ()
		{
			return Task.Run (() => {
				ThreadUtils.Sleep(2000);
				return new ProfileInformation();
			});
		}

		#endregion
	}
}