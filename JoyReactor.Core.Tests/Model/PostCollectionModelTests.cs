using System.Linq;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using JoyReactor.Core.Tests.Inner;

namespace JoyReactor.Core.Tests.Model
{
	[TestFixture]
	public class PostCollectionModelTests
	{
		PostCollectionModel module;

		[SetUp]
		public void SetUp ()
		{
			var provider = new DefaultServiceLocator (new TestModule ());
			ServiceLocator.SetLocatorProvider (() => provider);
			module = new PostCollectionModel ();
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
			AssertFirstOrder (actual);

			SetFakeSite ("http://joyreactor.cc/tag/песочница", "joyreactor_pesochnica_2.html");
			module.SyncFirstPage (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (10, actual.Posts.Count);
			Assert.AreEqual (8, actual.NewItemsCount);
			Assert.AreEqual (0, actual.DividerPosition);
			AssertFirstOrder (actual);

			module.ApplyNewItems (testId).Wait ();

			actual = module.Get (testId).Result;
			Assert.AreEqual (18, actual.Posts.Count);
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (10, actual.DividerPosition);
			AssertApplyOrder (actual);
		}

		static void AssertFirstOrder(PostCollectionState actual)
		{
			AssertOrder (new string[] {
				"1623109",
				"1624655",
				"1624643",
				"1624629",
				"1624613",
				"1624612",
				"1624609",
				"1624603",
				"1624593",
				"1624585"
			}, actual);
		}

		static void AssertApplyOrder (PostCollectionState actual)
		{
			AssertOrder (new string[] {
				"1624754",
				"1624728",
				"1624703",
				"1624701",
				"1624694",
				"1624689",
				"1624669",
				"1624665",
				"1624655",
				"1624643",
				"1623109",
				"1624629",
				"1624613",
				"1624612",
				"1624609",
				"1624603",
				"1624593",
				"1624585"
			}, actual);
		}

		static void AssertOrder (string[] expected, PostCollectionState actual)
		{
			CollectionAssert.AreEqual (expected, actual.Posts.Select (s => s.PostId.Split ('-') [1]));
		}

		[Test]
		public void TestDoubleSyncSamePage ()
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