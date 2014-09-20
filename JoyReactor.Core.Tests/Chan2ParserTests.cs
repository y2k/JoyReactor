using NUnit.Framework;
using System;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Tests.Inner;

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

			parser.ExtractPost ("a,1755718",
				state => {
					// TODO
				});
		}
    }
}