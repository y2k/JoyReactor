using NUnit.Framework;
using System;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model.Parser;
using System.Text.RegularExpressions;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class Chan4ParserTests
	{
		private SiteParser parser;

		[Test]
		public void TestGetThread572092321 ()
		{
			parser.NewPost += (sender, e) => {
				// TODO
			};
			parser.NewComment+= (sender, e) => {
				// TODO
			};
			parser.ExtractPost ("b,572092321");
		}

		[Test]
		public void GetPostsFromB ()
		{
			int actualPostCount = 0;
			parser.ExtractTagPostCollection (ID.TagType.Good, "b", 0, null, state => {

				Assert.IsNotNull (state);
				if (state.State == CollectionExportState.ExportState.PostItem) {
					actualPostCount++;
					// TODO: дописать тест
					Assert.IsTrue (Regex.IsMatch (state.Post.Id, @"b,\d+"), "Post id = " + state.Post.Id);
				}

			});

			Assert.AreEqual (15, actualPostCount);
		}

		[Test]
		public void TestBPages ()
		{
			for (int i = 0; i < 2; i++)
				parser.ExtractTagPostCollection (ID.TagType.Good, "b", i, null, state => {
					// Ignore
				});
		}

		[SetUp]
		public void Setup ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new TestModule ()));
			parser = new Chan4Parser ();
		}

		[Test]
		public void Chan4_GetPosts_WSG ()
		{
			parser.ExtractTagPostCollection (ID.TagType.Good, "wsg", 0, null, state => {
				Assert.IsNotNull (state);
				// TODO: дописать тест
			});
		}
	}
}