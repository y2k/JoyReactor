using System;
using NUnit.Framework;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;

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
		public void TestGet ()
		{
			var actual = module.Get (TestId).Result;
			Assert.AreEqual (0, actual.NewItemsCount);
			Assert.AreEqual (0, actual.Posts.Count);

			module.SyncFirstPage (TestId).Wait ();

//			var z = ServiceLocator.Current.GetInstance<ISQLiteConnection>().ExecuteScalar<int> ("SELECT COUNT(*) FROM posts");
//			z.ToString ();

			actual = module.Get (TestId).Result;
			Assert.AreEqual (10, actual.NewItemsCount);
			Assert.AreEqual (10, actual.Posts.Count);
		}
	}
}