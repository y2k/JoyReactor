using JoyReactor.Core.ViewModels;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    class LoginViewModelTests
    {
        LoginViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new LoginViewModel();
        }

        [Test]
        public void Test()
        {
            viewmodel.Username = "test-username";
            viewmodel.Password = "test-password";
            viewmodel.LoginCommand.Execute(null);
        }
    }
}