using GalaSoft.MvvmLight.Messaging;
using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Threading;

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

        [Test, Timeout(10000)]
        public void Test()
        {
            var locker = new ManualResetEvent(false);
            Messenger.Default.Register<ProfileViewModel.NavigateToLoginMessage>(this, m => locker.Set());

            SynchronizationContext.SetSynchronizationContext(new SynchronizationContext());
            viewmodel.Initialize();
            SynchronizationContext.SetSynchronizationContext(null);

            Assert.IsTrue(viewmodel.IsLoading);
            locker.WaitOne();
            Assert.IsFalse(viewmodel.IsLoading);
        }
    }
}