using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    class FeedViewModelTests
    {
        FeedViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new FeedViewModel();
        }

        [Test]
        public async void Test()
        {
            viewmodel.Initialize(ID.Factory.New(ID.IdConst.ReactorGood));
            await Delay();

            Assert.AreEqual(10, viewmodel.Posts.Count);
            CollectionAssert.AreEqual(
                new[] { "2025656", "2025491", }.Select(s => s == null ? null : $"http://img0.joyreactor.cc/pics/post/-{s}.png"),
                viewmodel.Posts.OfType<FeedViewModel.ContentViewModel>().Select(s => s.Image));
        }

        Task Delay()
        {
            return Task.Delay(400);
        }
    }
}