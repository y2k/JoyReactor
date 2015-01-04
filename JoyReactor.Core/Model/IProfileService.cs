using System;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model
{
    interface IProfileService
    {
        Task<MyProfile> GetMyProfile();

        Task Login(string username, string password);

        Task Logout();
    }

    public class MyProfile
    {
        public string Username { get; set; }

        public float Rating { get; set; }
    }

    public class NotLogedException : Exception { }
}