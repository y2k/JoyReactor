using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels;
using Microsoft.Reactive.Testing;
using Moq;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class TagInformationViewModelTests
    {
        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
        }

        Task SaveLinkedTagsToDatabase(ID id)
        {
            return JoyReactorProvider.Create().LoadTagAndPostListAsync(id, Mock.Of<JoyReactorProvider.IListStorage>());
        }

        [Test]
        public async void Test()
        {
            var scheduler = new TestScheduler();
            TagCollectionModel.DefaultScheduler = scheduler;
            var controller = new TagInformationViewModel { UiScheduler = scheduler };

            var id = ID.Factory.NewTag("комиксы");

            await SaveLinkedTagsToDatabase(id);
            controller.ChangeCurrentTag(id);

            scheduler.AdvanceBy(2);

            CollectionAssert.AreEqual(
                new []
                { 
                    "countryballs", 
                    "Cyanide and happiness",
                    "корейские комиксы",
                    "фотокомиксы",
                    "Dilbert",
                    "texic",
                    "cynic mansion",
                    "оглаф",
                    "WUMO",
                    "SMBC",
                    "бассейн",
                    "черепаха",
                    "вот это поворот",
                    "Vaygr",
                    "Gnar",
                },
                controller.Items.Select(s => s.Title).ToArray());

            Assert.IsTrue(controller.Items.Take(10).All(s => s.Group == "Популярные Комиксы"));
            Assert.IsTrue(controller.Items.Skip(10).All(s => s.Group == "Интересное"));
        }
    }
}