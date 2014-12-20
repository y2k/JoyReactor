using JoyReactor.Core.Model;
using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class GalleryViewModelTests
    {
        GalleryViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new GalleryViewModel();
        }

        [Test]
        public async Task Test()
        {
            int id = await SetUpPost();

            await viewmodel.Initialize(id);

            Assert.AreEqual(12, viewmodel.Images.Count);
            foreach (var s in viewmodel.Images)
            {
                Assert.IsNotNull(s);
                Assert.IsTrue(s.StartsWith("http://"), s);
            }
        }

        private static async Task<int> SetUpPost()
        {
            int id = TestExtensions.CreatePostIdDatabase(ID.SiteParser.JoyReactor, "861529");
            await new PostModel().GetPostAsync(id);
            return id;
        }
    }
}