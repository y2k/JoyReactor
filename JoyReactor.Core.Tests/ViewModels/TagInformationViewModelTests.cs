using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Helpers;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System.Linq;
using System.Threading.Tasks;

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
		}

		Task SaveLinkedTagsToDatabase (ID id)
		{
			return new PostCollectionModel ().SyncFirstPage (id);
		}

		[Test]
		public async Task Test ()
		{
            var controller = new TagInformationViewModel();
			var id = ID.Factory.NewTag ("комиксы");

			await SaveLinkedTagsToDatabase (id);
			await controller.ChangeCurrentTag (id);

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