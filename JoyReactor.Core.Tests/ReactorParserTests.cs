using NUnit.Framework;
using System;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Inject;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class ReactorParserTests
	{
		[Test]
		public void TestCase ()
		{
			InjectService.Initialize ();

			var parser = new ReactorParser ();
			parser.ExtractTagPostCollection(ID.TagType.Good, null, 0, 
				s =>
				{
				});
		}
	}
}

