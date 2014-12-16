using NUnit.Framework;
using System;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class Chan2ParserTests
	{
		SiteParser parser;

		[SetUp]
		public void SetUp ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new TestModule ()));
			parser = new Chan2Parser ();
		}

		[Test]
		public void Chan2_GetPosts_B ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag ("b", ID.TagType.Good, 0);
		}

		[Test]
		public void Chan2_GetPosts_MEDIA ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag ("media", ID.TagType.Good, 0);
		}

		[Test]
		public void TestPost1755718 ()
		{
			int commentCount = 0, imageCount = 0;
			parser.NewComment += (sender, state) => {
				commentCount++;
				imageCount += state.Attachments.Length;
			};
			parser.ExtractPost ("a,1755718");

			Assert.IsTrue (commentCount >= 141, "Comment count = " + commentCount);
			Assert.IsTrue (imageCount >= 70, "Image count = " + imageCount);
		}
	}
}