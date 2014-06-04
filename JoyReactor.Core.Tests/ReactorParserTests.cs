using NUnit.Framework;
using System;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Inject;
using System.Linq;
using System.Text.RegularExpressions;
using System.Collections.Generic;

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

			bool wasBegin = false;
			bool wasInfo = false;
			int commenCount = 0;
			int commentsAttachments = 0;
			var comIds = new HashSet<string> ();

			parser.ExtractPost ("861529", 
				s => {
					// TODO
					if (s.State == PostExportState.ExportState.Begin) {
						wasBegin = true;
					} else if (s.State == PostExportState.ExportState.Info) {
						wasInfo = true;

						Assert.AreEqual("mr.viridis", s.userName);
						Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-770859.jpeg", s.image);
						Assert.AreEqual(551, s.imageWidth);
						Assert.AreEqual(817, s.imageHeight);
						Assert.AreEqual("http://img0.joyreactor.cc/images/default_avatar.jpeg", s.userImage);
					} else if (s.State == PostExportState.ExportState.Comment) {
						Assert.IsNotNull(s.Comment);
						commenCount++;

						Assert.IsTrue(s.Comment.created <= 1577836800000L && s.Comment.created >= 1262304000000L, "Comment created = " + s.Comment.created);
						Assert.IsTrue(Regex.IsMatch(s.Comment.id, "\\d+"), "Comment id = " + s.Comment.id);
						comIds.Add(s.Comment.id);
						if (s.Comment.parentId != null) Assert.IsTrue(comIds.Contains(s.Comment.parentId), "Comment parent id = " + s.Comment.parentId);

						var t=s.Comment.text;
						Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Comment.text);

						Assert.NotNull(s.Comment.userName);
						Assert.IsTrue(Regex.IsMatch(s.Comment.userName, "[\\w\\d_]+"), "Comment user name = " + s.Comment.userName);
						Assert.IsNotNull(s.Comment.userImage);
						Assert.IsTrue(Regex.IsMatch(s.Comment.userImage, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.Comment.userImage);

						Assert.IsNotNull(s.Comment.attachments);
						commentsAttachments += s.Comment.attachments.Length;
						foreach (var z in s.Comment.attachments) {
							Assert.IsTrue(Regex.IsMatch(z.imageUrl, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Comment.id + ", url = " + z.imageUrl);
						}
					}
				});

			Assert.IsTrue (wasBegin);
			Assert.IsTrue (wasInfo);
			Assert.IsTrue (commenCount >= 44, "Comment count = " + commenCount);
			Assert.IsTrue (commentsAttachments >= 13, "Total attachments = " + commentsAttachments);
		}
	}
}