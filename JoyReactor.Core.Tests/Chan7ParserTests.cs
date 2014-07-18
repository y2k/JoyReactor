using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests
{
    [TestFixture()]
    public class Chan7ParserTests
    {
        [Test()]
        public void Chan7_GetPosts_B()
        {
            ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator());

            var parser = new Chan7Parser();
            parser.ExtractTagPostCollection(ID.TagType.Good, "b", 0, null, state =>
            {

                Assert.IsNotNull(state);

            });
        }

        [Test()]
        public void Chan7_GetPosts_GIF()
        {
            ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator());

            var parser = new Chan7Parser();
            parser.ExtractTagPostCollection(ID.TagType.Good, "gif", 0, null, state =>
            {

                Assert.IsNotNull(state);

            });
        }
    }
}