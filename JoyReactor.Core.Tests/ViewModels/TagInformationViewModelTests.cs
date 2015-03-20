using System.Linq;
using System.Threading.Tasks;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Tests.Helpers;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using Microsoft.Reactive.Testing;
using Moq;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
	public class TagInformationViewModelTests
	{
		[SetUp]
		public void SetUp ()
		{
			// TODO:
			var provider = new DefaultServiceLocator (new TestModule ());
			ServiceLocator.SetLocatorProvider (() => provider);
            //SynchronizationContext.SetSynchronizationContext(new SynchronizationContext());
        }

        [TearDown]
        public void TestDown()
        {
            //SynchronizationContext.SetSynchronizationContext(null);
        }

		Task SaveLinkedTagsToDatabase (ID id)
		{
//			return new PostCollectionModel ().SyncFirstPage (id);
            return JoyReactorProvider.Create().LoadTagAndPostListAsync(id, Mock.Of<JoyReactorProvider.IListStorage>());
		}

		[Test]
        public async void Test ()
		{
            var scheduler = new TestScheduler();
            TagCollectionModel.DefaultScheduler = scheduler;
            var controller = new TagInformationViewModel { UiScheduler = scheduler };

			var id = ID.Factory.NewTag ("комиксы");

			await SaveLinkedTagsToDatabase (id);
            controller.ChangeCurrentTag (id);

            scheduler.AdvanceBy(2);

			CollectionAssert.AreEqual (
				new [] { 
					"countryballs", 
					"Комиксы Cyanide and happiness",
					"корейские комиксы",
					"фотокомиксы",
					"Dilbert",
					"texic",
					"cynic mansion",
					"оглаф",
					"WUMO",
					"SMBC",
				},
				controller.Items.Select (s => s.Title).ToArray ());

// TODO: Вернуть проверку групп
//			Assert.IsTrue (controller.Items.All (s => s.Group == "Популярные Комиксы"),
//				"Group = " + controller.Items.First (s => s.Group != "Популярные Комиксы").Group);
		}
	}
}