using System.Linq;
using NUnit.Framework;
using JoyReactor.Core.Controllers;
using System.Threading.Tasks;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Inner;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Tests.Controllers
{
	[TestFixture]
	public class TagInformationControllerTests
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
			var controller = new TagInformationController {
				InvalidateUiCallback = () => {
				},
			};
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