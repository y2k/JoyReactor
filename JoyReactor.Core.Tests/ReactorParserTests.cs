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

		[Test]
		public void TestComics ()
		{
			InjectService.Initialize ();

			int linkedTagCount = 0;

			var parser = new ReactorParser ();
			parser.ExtractTagPostCollection(ID.TagType.Good, "комиксы", 0, 
				s =>
				{
					if (s.State == CollectionExportState.ExportState.LikendTag) {
						linkedTagCount++;
					}
				});

			Assert.True (linkedTagCount > 0, "Linked tags = " + linkedTagCount);
		}

		[Test]
		public void testPost861529()
		{
			InjectService.Initialize ();
			var parser = new ReactorParser ();

			parser.ExtractPost ("861529", 
				s => {
					// TODO
				});
		}
	}
}