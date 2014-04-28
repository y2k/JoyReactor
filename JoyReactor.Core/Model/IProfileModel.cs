using System;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
	public interface IProfileModel
	{
		Task LoginAsync(string username, string password);

		Task LogoutAsync();

		Task<ProfileInformation> GetCurrentProfileAsync();
	}

	public class ProfileInformation
	{
		// TODO
	}
}