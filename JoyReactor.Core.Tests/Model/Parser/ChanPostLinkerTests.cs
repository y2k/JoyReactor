using System;
using NUnit.Framework;
using JoyReactor.Core.Model.Parser;
using HtmlAgilityPack;
using System.Linq;

namespace JoyReactor.Core.Tests.Model.Parser
{
	[TestFixture]
	public class ChanPostLinkerTests
	{
		[Test]
		public void Test ()
		{
			var module = new ChanPostLinker ("78271552");
			var node = HtmlNode.CreateNode ("<div class=\"pst\">[/B]ТРЕДШОТОВ ТХРЕД[/B]<br>бампаю схоронёнными и выкатываюсь, если не взлетит</div>");

			var actual = module.Export (node, "78271552");
			var expected = new ChanPostLinker.PartialPost[] {
				new ChanPostLinker.PartialPost {
					Content = "[/B]ТРЕДШОТОВ ТХРЕД[/B]<br>бампаю схоронёнными и выкатываюсь, если не взлетит",
					Id = "78271552-0",
					ParentIds = null,
				}
			};
			CollectionAssert.AreEqual (expected, actual);

			node = HtmlNode.CreateNode ("<div class=\"pst\"><a href=\"/b/res/78271552.html#78271552\">>>78271552</a><br>Разметку проебал, сука</div>");
			actual = module.Export (node, "78271584");
			expected = new ChanPostLinker.PartialPost[] {
				new ChanPostLinker.PartialPost {
					Content = "Разметку проебал, сука",
					Id = "78271584-0",
					ParentIds = new string[] { "78271552-0" },
				}
			};
		}
	}
}