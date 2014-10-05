using System;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using JoyReactor.Core.Tests.Inner;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class PostModelTests
	{
		private const int TestPostId = 1;
		private IPostModel model;

		[SetUp]
		public void SetUp ()
		{
			var provider = new DefaultServiceLocator (new TestModule ());
			ServiceLocator.SetLocatorProvider (() => provider);
			model = ServiceLocator.Current.GetInstance<IPostModel> ();
		}

		[Test]
		public void GetTopComments ()
		{
			SyncMockPost ();

			var actual = model.GetTopCommentsAsync (TestPostId, 10).Result;

			Assert.IsNotNull (actual);
			Assert.AreEqual (10, actual.Count);
		}

		[Test]
		public void GetAttachments ()
		{
			SyncMockPost ();

			var actual = model.GetAttachmentsAsync (1).Result;

			Assert.IsNotNull (actual);
			Assert.AreEqual (89, actual.Count, "Count = " + actual.Count);

			foreach (var a in actual) {
				Assert.IsNotNull (a.Url);
				Assert.IsTrue (Uri.IsWellFormedUriString (a.Url, UriKind.Absolute));
			}
		}

		private void SyncMockPost ()
		{
			var conn = ServiceLocator.Current.GetInstance<ISQLiteConnection> ();
			conn.Insert (new Post {
				PostId = ID.SiteParser.Chan4 + "-b,572092321"
			});
			model.GetPostAsync (TestPostId).Wait ();
		}
	}
}