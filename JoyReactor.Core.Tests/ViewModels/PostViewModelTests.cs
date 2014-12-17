using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Threading.Tasks;

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
        public async Task LoadPost861529Test()
        {
            int id = TestExtensions.CreatePostIdDatabase(ID.SiteParser.JoyReactor, "861529");

            Assert.IsFalse(viewmodel.IsBusy);
            viewmodel.Initialize(id).GetAwaiter();
            Assert.IsTrue(viewmodel.IsBusy);

            await Task.Delay(300);
            Assert.IsFalse(viewmodel.IsBusy);

            Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-770859.jpeg", viewmodel.Image);
            Assert.AreEqual(13, viewmodel.Comments.Count);
        }
    }
}