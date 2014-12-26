using JoyReactor.Core.ViewModels;
using NUnit.Framework;

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

        [Test]
        public void Test()
        {
            //
            viewmodel.Initialize();
        }
    }
}