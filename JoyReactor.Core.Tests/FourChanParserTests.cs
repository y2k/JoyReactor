using NUnit.Framework;
using System;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;

namespace JoyReactor.Core.Tests
{
	[TestFixture ()]
	public class FourChanParserTests
	{
		[Test ()]
		public void FourChan_GetPosts_B ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator ());

			var parser = new FourChanParser ();
			parser.ExtractTagPostCollection (ID.TagType.Good, "b", 0, null, state => {

                Assert.IsNotNull(state);

			});
		}
    
        [Test()]
        public void FourChan_GetPosts_WSG()
        {
            ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator());

            var parser = new FourChanParser();
            parser.ExtractTagPostCollection(ID.TagType.Good, "wsg", 0, null, state =>
            {

                Assert.IsNotNull(state);

            });
        }
    }
}