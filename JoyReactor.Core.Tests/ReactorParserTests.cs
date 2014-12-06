using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Parser;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using JoyReactor.Core.Tests.Inner;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class ReactorParserTests
	{
		SiteParser parser;

		[SetUp]
		public void SetUp ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new TestModule ()));
			parser = new ReactorParser ();
			parser.Cookies = new Dictionary<string,string> ();
		}

		[Test]
		public void TestFeatured ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag (null, ID.TagType.Good, 0);
		}

		[Test]
		public void TestComics ()
		{
			int linkedTagCount = 0;

			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.NewLinkedTag += (sender, e) => {
				Assert.IsNotNull (e);
				linkedTagCount++;
			};
			parser.ExtractTag ("комиксы", ID.TagType.Good, 0);

			Assert.True (linkedTagCount > 0, "Linked tags = " + linkedTagCount);
		}

		[Test]
		[Ignore]
		public void TestPost861529 ()
		{
			bool wasInfo = false;
			int commenCount = 0;
			int commentsAttachments = 0;
			var comIds = new HashSet<string> ();
			float sumCommentRating = 0;
			int notRootCommentCount = 0;

			parser.NewPostInformation += (sender, s) => {
				wasInfo = true;

				Assert.AreEqual ("mr.viridis", s.User.Name);
				Assert.AreEqual ("http://img0.joyreactor.cc/pics/post/-770859.jpeg", s.Attachments [0].Image);
				Assert.AreEqual (551, s.Attachments [0].Width);
				Assert.AreEqual (817, s.Attachments [0].Height);
				Assert.AreEqual ("http://img0.joyreactor.cc/images/default_avatar.jpeg", s.User.Avatar);
			};
			parser.NewComment += (sender, s) => {
				Assert.IsNotNull (s);
				commenCount++;

				sumCommentRating += s.Rating;

				Assert.IsTrue (
					s.Created <= 1577836800000L.DateTimeFromUnixTimestamp ()
					&& s.Created >= 1262304000000L.DateTimeFromUnixTimestamp (), 
					"Comment created = " + s.Created);
				Assert.IsTrue (Regex.IsMatch (s.Id, "\\d+"), "Comment id = " + s.Id);
				comIds.Add (s.Id);
				if (s.ParentIds != null)
					foreach (var c in s.ParentIds) {
						Assert.IsTrue (comIds.Contains (c), "Comment parent id = " + c);
						notRootCommentCount++;
					}

				var t = s.Content;
				Assert.IsTrue (t == null || t == t.Trim (), "Comment text = " + s.Content);
				TestCommentTextNotContainsTag (s.Content);

				Assert.NotNull (s.User.Name);
				Assert.IsTrue (Regex.IsMatch (s.User.Name, "[\\w\\d_]+"), "Comment user name = " + s.User.Name);
				Assert.IsNotNull (s.User.Avatar);
				Assert.IsTrue (Regex.IsMatch (s.User.Avatar, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.User.Avatar);

				Assert.IsNotNull (s.Attachments);
				commentsAttachments += s.Attachments.Length;
				foreach (var z in s.Attachments) {
					Assert.IsTrue (Regex.IsMatch (z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z.Image);
				}
			};
			parser.ExtractPost ("861529");

			Assert.IsTrue (wasInfo);
			Assert.IsTrue (commenCount >= 44, "Comment count = " + commenCount);
			Assert.IsTrue (commentsAttachments >= 12, "Total attachments = " + commentsAttachments);
			Assert.IsTrue (sumCommentRating > 50, "Sum comment rating = " + sumCommentRating);
			Assert.IsTrue (notRootCommentCount > 30, "Not root comment count = " + notRootCommentCount);
			Assert.IsTrue (commenCount - notRootCommentCount > 10, "Root comment count = " + (commenCount - notRootCommentCount));
		}

		[Test]
		[Ignore]
		public void TestPost1323757 ()
		{
			bool wasInfo = false;
			int commenCount = 0;
			int commentsAttachments = 0;
			var comIds = new HashSet<string> ();
			float sumCommentRating = 0;
			int notRootCommentCount = 0;

			parser.NewPostInformation += (sender, s) => {
				wasInfo = true;
				
				Assert.AreEqual ("Dendy-A-", s.User.Name);
				Assert.AreEqual ("http://img0.joyreactor.cc/pics/avatar/user/47722", s.User.Avatar);
				Assert.AreEqual ("http://img0.joyreactor.cc/pics/post/-1240714.jpeg", s.Attachments [0].Image);
				Assert.AreEqual (195, s.Attachments [0].Width);
				Assert.AreEqual (208, s.Attachments [0].Height);
			};
			parser.NewComment += (sender, s) => {
				Assert.IsNotNull (s);
				commenCount++;
				
				sumCommentRating += s.Rating;
				
				Assert.IsTrue (
					s.Created <= 1577836800000L.DateTimeFromUnixTimestamp ()
					&& s.Created >= 1262304000000L.DateTimeFromUnixTimestamp (),
					"Comment created = " + s.Created);
				Assert.IsTrue (Regex.IsMatch (s.Id, "\\d+"), "Comment id = " + s.Id);
				comIds.Add (s.Id);
				if (s.ParentIds != null)
					foreach (var c in s.ParentIds) {
						Assert.IsTrue (comIds.Contains (c), "Comment parent id = " + c);
						notRootCommentCount++;
					}

				var t = s.Content;
				Assert.IsTrue (t == null || t == t.Trim (), "Comment text = " + s.Content);
				TestCommentTextNotContainsTag (s.Content);
				
				Assert.NotNull (s.User.Name);
				Assert.IsTrue (Regex.IsMatch (s.User.Name, "[\\w\\d_]+"), "Comment user name = " + s.User.Name);
				Assert.IsNotNull (s.User.Avatar);
				Assert.IsTrue (Regex.IsMatch (s.User.Avatar, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.User.Avatar);
				
				Assert.IsNotNull (s.Attachments);
				commentsAttachments += s.Attachments.Length;
				foreach (var z in s.Attachments) {
					Assert.IsTrue (Regex.IsMatch (z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z.Image);
				}
			};
			parser.ExtractPost ("1323757");

			Assert.IsTrue (wasInfo);
			Assert.IsTrue (commenCount >= 2520, "Comment count = " + commenCount);
			Assert.IsTrue (commentsAttachments >= 577, "Total attachments = " + commentsAttachments);
			Assert.IsTrue (sumCommentRating > 1500, "Sum comment rating = " + sumCommentRating);
			Assert.IsTrue (notRootCommentCount > 2000, "Not root comment count = " + notRootCommentCount);
			Assert.IsTrue (commenCount - notRootCommentCount > 500, "Root comment count = " + (commenCount - notRootCommentCount));
		}

		[Test]
		[Ignore]
		public void TestPost1382511 ()
		{
			bool wasInfo = false;
			int commenCount = 0;
			int commentsAttachments = 0;
			var comIds = new HashSet<string> ();
			float sumCommentRating = 0;
			int notRootCommentCount = 0;
			var commentTexts = new HashSet<string> ();

			parser.NewPostInformation += (sender, s) => {
				Assert.AreEqual ("Mishvanda", s.User.Name);
				Assert.AreEqual ("http://img0.joyreactor.cc/pics/avatar/user/145422", s.User.Avatar);
				Assert.AreEqual ("http://img0.joyreactor.cc/pics/post/-1316599.gif", s.Attachments [0].Image);
				Assert.AreEqual (600, s.Attachments [0].Width);
				Assert.AreEqual (750, s.Attachments [0].Height);
			};
			parser.NewComment += (sender, s) => {
				Assert.IsNotNull (s);
				commenCount++;
				
				sumCommentRating += s.Rating;
				
				Assert.IsTrue (
					s.Created <= 1577836800000L.DateTimeFromUnixTimestamp ()
					&& s.Created >= 1262304000000L.DateTimeFromUnixTimestamp (),
					"Comment created = " + s.Created);
				Assert.IsTrue (Regex.IsMatch (s.Id, "\\d+"), "Comment id = " + s.Id);
				comIds.Add (s.Id);
				if (s.ParentIds != null)
					foreach (var c in s.ParentIds) {
						Assert.IsTrue (comIds.Contains (c), "Comment parent id = " + c);
						notRootCommentCount++;
					}
				
				var t = s.Content;
				Assert.IsTrue (t == null || t == t.Trim (), "Comment text = " + s.Content);
				TestCommentTextNotContainsTag (s.Content);
				
				Assert.NotNull (s.User.Name);
				Assert.IsTrue (Regex.IsMatch (s.User.Name, "[\\w\\d_]+"), "Comment user name = " + s.User.Name);
				Assert.IsNotNull (s.User.Avatar);
				Assert.IsTrue (Regex.IsMatch (s.User.Avatar, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.User.Avatar);
				
				Assert.IsNotNull (s.Attachments);
				commentsAttachments += s.Attachments.Length;
				foreach (var z in s.Attachments) {
					Assert.IsTrue (Regex.IsMatch (z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z.Image);
				}
				
				if (!string.IsNullOrEmpty (s.Content)) {
					Assert.IsFalse (commentTexts.Contains (s.Content), "Text = " + s.Content);
					commentTexts.Add (s.Content);
				}
			};
			parser.ExtractPost ("1382511");

			Assert.IsTrue (wasInfo);
			Assert.IsTrue (commenCount >= 38, "Comment count = " + commenCount);
			Assert.IsTrue (commentsAttachments >= 7, "Total attachments = " + commentsAttachments);
			Assert.IsTrue (sumCommentRating >= 62, "Sum comment rating = " + sumCommentRating);
			Assert.IsTrue (notRootCommentCount >= 22, "Not root comment count = " + notRootCommentCount);
			Assert.IsTrue (commenCount - notRootCommentCount >= 16, "Root comment count = " + (commenCount - notRootCommentCount));
		}

		private static void TestCommentTextNotContainsTag (string text)
		{
			var t = text == null ? null : text.ToLower ();
			Assert.IsFalse (t != null && new string[] {
				"<br>", "<br />", "<p>", "<a ", "</a>"
			}.Any (a => t.Contains (a)), "Comment = " + text);
		}
	}
}