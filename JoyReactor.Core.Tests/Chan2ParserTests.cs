using NUnit.Framework;
using System;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Tests
{
	[TestFixture ()]
	public class Chan2ParserTests
	{
		[Test ()]
		public void Chan2_GetPosts_B ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator ());

			var parser = new Chan2Parser ();
			parser.ExtractTagPostCollection (ID.TagType.Good, "b", 0, null, state => {

                Assert.IsNotNull(state);

			});
		}
    
        [Test()]
        public void Chan2_GetPosts_MEDIA()
        {
            ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator());

            var parser = new Chan2Parser();
            parser.ExtractTagPostCollection(ID.TagType.Good, "media", 0, null, state =>
            {

                Assert.IsNotNull(state);

            });
        }

		[Test]
		public void TestPost1755718()
		{
			ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator(new TestModule()));

			var parser = new Chan2Parser();

			int commentCount = 0;
			int imageCount = 0;
			parser.ExtractPost ("a,1755718",
				state => {
					// TODO
					if (state.State == PostExportState.ExportState.Comment) {
						commentCount++;
						imageCount += state.Comment.Attachments.Length;
					}
				});
			Assert.IsTrue (commentCount >= 141, "Comment count = " + commentCount);
			Assert.IsTrue (imageCount >= 70, "Image count = " + imageCount);
		}
    }
}