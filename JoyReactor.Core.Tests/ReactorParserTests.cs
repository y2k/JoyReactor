using NUnit.Framework;
using System;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class ReactorParserTests
	{
		[Test]
		public void TestCase ()
		{
			var parser = new ReactorParser ();
			parser.ExtractTagPostCollection(ID.SITE_REACTOR, null, 0, 
				s =>
				{
				});
		}
	}
}

