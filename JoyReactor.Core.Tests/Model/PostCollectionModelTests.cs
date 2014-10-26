using NUnit.Framework;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model;

namespace JoyReactor.Core.Tests.Model
{
	[TestFixture]
	public class PostCollectionModelTests
	{
		static readonly ID TestId = ID.Factory.New (ID.IdConst.ReactorGood);
		IPostCollectionModel module;

		[SetUp]
		public void SetUp ()
		{
			var provider = new DefaultServiceLocator (new TestModule ());
			ServiceLocator.SetLocatorProvider (() => provider);
			module = ServiceLocator.Current.GetInstance<IPostCollectionModel> ();
		}

		[Test]
		public void TestGetFirstOpen ()
		{
			var actual = module.Get (TestId).Result;
			Assert.AreEqual (0, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (0, actual.DividerPosition);

			module.SyncFirstPage (TestId).Wait ();

			actual = module.Get (TestId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);

			module.SyncNextPage (TestId).Wait ();

			actual = module.Get (TestId).Result;
			Assert.AreEqual (20, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (20, actual.DividerPosition);

			module.Reset (TestId).Wait ();

			actual = module.Get (TestId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);
		}
	}
}