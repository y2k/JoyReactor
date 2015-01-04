using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class ProfileViewModelTests
    {
        ProfileViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new ProfileViewModel();
        }

        [TearDown]
        public void TearDown()
        {
            Messenger.Default.Unregister(this);
            viewmodel.Cleanup();
        }

        [Test]
        public async Task TestFailLoadProfile()
        {
            bool navigateToLoginRequested = false;
            Messenger.Default.Register<ProfileViewModel.NavigateToLoginMessage>(this, m => navigateToLoginRequested = true);

            var task = viewmodel.Initialize();
            Assert.IsTrue(viewmodel.IsLoading);
            await task;
            Assert.IsFalse(viewmodel.IsLoading);
            Assert.IsTrue(navigateToLoginRequested);
        }
    }
}