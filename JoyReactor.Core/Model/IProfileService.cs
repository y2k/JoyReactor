using JoyReactor.Core.Model.DTO;
using System;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    interface IProfileService
    {
        Task<Profile> GetMyProfile();

        Task Login(string username, string password);

        Task Logout();
    }

    public class NotLogedException : Exception
    {
    }
}