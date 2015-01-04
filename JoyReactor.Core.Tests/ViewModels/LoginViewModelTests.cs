using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Threading.Tasks;

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

        [TearDown]
        public void TestDown()
        {
            TestExtensions.TearDown(this);
        }

        [Test]
        public async Task LoginNotExistsUser()
        {
            viewmodel.Username = "fail-username";
            viewmodel.Password = "fail-password";
            var task = viewmodel.Login();
            Assert.IsTrue(viewmodel.IsBusy);
            await task;
            Assert.IsFalse(viewmodel.IsBusy);
            Assert.IsTrue(viewmodel.HasError);
        }

        [Test]
        public async Task LoginExistsUser()
        {
            bool navigationToProfileRequested = false;
            Messenger.Default.Register<LoginViewModel.NavigateToProfileMessage>(this, _ => navigationToProfileRequested = true);

            viewmodel.Username = "mykie78";
            viewmodel.Password = "success-password";
            var task = viewmodel.Login();
            Assert.IsTrue(viewmodel.IsBusy);
            await task;
            Assert.IsFalse(viewmodel.IsBusy);
            Assert.IsFalse(viewmodel.HasError);
            Assert.IsTrue(navigationToProfileRequested);
        }
    }
}