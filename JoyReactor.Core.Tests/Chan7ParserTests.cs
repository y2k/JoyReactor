using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class Chan7ParserTests
	{
		private SiteParser parser;

		[SetUp]
		public void SetUp ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new TestModule ()));
			parser = new Chan7Parser ();
		}

		[Test]
		public void Chan7_GetPosts_B ()
		{
			parser.ExtractTagPostCollection (ID.TagType.Good, "b", 0, null, state => {

				Assert.IsNotNull (state);

			});
		}

		[Test]
		public void TestNextPage ()
		{
			for (int i = 0; i < 2; i++) {
				parser.ExtractTagPostCollection (ID.TagType.Good, "b", i, null, state => {
					Assert.IsNotNull (state);
				});
			}
		}

		[Test]
		public void Chan7_GetPosts_GIF ()
		{
			parser.ExtractTagPostCollection (ID.TagType.Good, "gif", 0, null, state => {

				Assert.IsNotNull (state);

			});
		}
	}
}