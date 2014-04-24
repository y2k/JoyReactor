using NUnit.Framework;
using System;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Inject;
using Ninject;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class ReactorParserTests
	{
		[Test]
		public void TestCase ()
		{
			InjectService.Initialize (new StandardKernel());

			var parser = new ReactorParser ();
			parser.ExtractTagPostCollection(ID.TagType.Good, null, 0, 
				s =>
				{
				});
		}
	}
}

