using JoyReactor.Core.ViewModels;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class PostViewModelTests
    {
        PostViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new PostViewModel();
        }

        [Test]
        public void LoadPost861529Test()
        {
            //
        }
    }
}