using NUnit.Framework;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Web;

namespace JoyReactor.Core.Tests.Model
{
	[TestFixture]
	public class PostCollectionModelTests
	{
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
			var testId = ID.Factory.New (ID.IdConst.ReactorGood);

			var actual = module.Get (testId).Result;
			Assert.AreEqual (0, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (0, actual.DividerPosition);

			module.SyncFirstPage (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);

			module.SyncNextPage (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (20, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (20, actual.DividerPosition);

			module.Reset (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);
		}

		[Test]
		public void TestApply ()
		{
			var testId = ID.Factory.NewTag ("песочница");

			var actual = module.Get (testId).Result;
			Assert.AreEqual (0, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (0, actual.DividerPosition);

			module.SyncFirstPage (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);

			SetFakeSite ("http://joyreactor.cc/tag/песочница", "joyreactor_pesochnica_2.html");
			module.SyncFirstPage (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (8, actual.NewItemsCount);
			Assert.AreEqual (0, actual.DividerPosition);

			module.ApplyNewItems (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (18, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);
		}

		[Test]
		public void TestDoubleSync ()
		{
			var testId = ID.Factory.NewTag ("песочница");
			module.SyncFirstPage (testId).Wait ();
			SetFakeSite ("http://joyreactor.cc/tag/песочница", "joyreactor_pesochnica_2.html");

			module.SyncFirstPage (testId).Wait ();
			module.SyncFirstPage (testId).Wait ();

			var actual = module.Get (testId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (8, actual.NewItemsCount);
			Assert.AreEqual (0, actual.DividerPosition);
		}

		void SetFakeSite (string url, string filename)
		{
			var downloader = (MockWebDownloader)ServiceLocator.Current.GetInstance<IWebDownloader> ();
			downloader.RouteUrls [url] = filename;
		}
	}
}