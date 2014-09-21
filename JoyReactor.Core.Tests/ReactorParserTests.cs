using NUnit.Framework;
using System;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Inject;
using System.Linq;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Tests.Inner;

namespace JoyReactor.Core.Tests
{
    [TestFixture]
    public class ReactorParserTests
    {
        [Test]
        public void TestFeatured()
        {
            ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator());

            var parser = new ReactorParser();
            parser.ExtractTagPostCollection(ID.TagType.Good, null, 0, new Dictionary<string, string>(),
                s =>
                {
                });
        }

        [Test]
        public void TestComics()
        {
            ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator());

            int linkedTagCount = 0;

            var parser = new ReactorParser();
            parser.ExtractTagPostCollection(ID.TagType.Good, "комиксы", 0, new Dictionary<string, string>(),
                s =>
                {
                    if (s.State == CollectionExportState.ExportState.LikendTag)
                    {
                        linkedTagCount++;
                    }
                });

            Assert.True(linkedTagCount > 0, "Linked tags = " + linkedTagCount);
        }

        [Test]
        public void TestPost861529()
        {
			ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator(new TestModule()));
            var parser = new ReactorParser();

            bool wasBegin = false;
            bool wasInfo = false;
            int commenCount = 0;
            int commentsAttachments = 0;
            var comIds = new HashSet<string>();
            float sumCommentRating = 0;
            int notRootCommentCount = 0;

            parser.ExtractPost("861529",
                s =>
                {
                    // TODO
                    if (s.State == PostExportState.ExportState.Begin)
                    {
                        wasBegin = true;
                    }
                    else if (s.State == PostExportState.ExportState.Info)
                    {
                        wasInfo = true;

                        Assert.AreEqual("mr.viridis", s.userName);
                        Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-770859.jpeg", s.image);
                        Assert.AreEqual(551, s.imageWidth);
                        Assert.AreEqual(817, s.imageHeight);
                        Assert.AreEqual("http://img0.joyreactor.cc/images/default_avatar.jpeg", s.userImage);
                    }
                    else if (s.State == PostExportState.ExportState.Comment)
                    {
                        Assert.IsNotNull(s.Comment);
                        commenCount++;

                        sumCommentRating += s.Comment.rating;

                        Assert.IsTrue(s.Comment.Created <= 1577836800000L && s.Comment.Created >= 1262304000000L, "Comment created = " + s.Comment.Created);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.id, "\\d+"), "Comment id = " + s.Comment.id);
                        comIds.Add(s.Comment.id);
						if (s.Comment.ParentIds != null) {
							foreach (var c in s.Comment.ParentIds) {
	                            Assert.IsTrue(comIds.Contains(c), "Comment parent id = " + c);
    	                        notRootCommentCount++;
							}
                        }

                        var t = s.Comment.text;
                        Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Comment.text);
                        TestCommentTextNotContainsTag(s.Comment.text);

                        Assert.NotNull(s.Comment.UserName);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.UserName, "[\\w\\d_]+"), "Comment user name = " + s.Comment.UserName);
                        Assert.IsNotNull(s.Comment.UserImage);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.UserImage, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.Comment.UserImage);

                        Assert.IsNotNull(s.Comment.Attachments);
                        commentsAttachments += s.Comment.Attachments.Length;
                        foreach (var z in s.Comment.Attachments)
                        {
                            Assert.IsTrue(Regex.IsMatch(z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Comment.id + ", url = " + z.Image);
                        }
                    }
                });

            Assert.IsTrue(wasBegin);
            Assert.IsTrue(wasInfo);
            Assert.IsTrue(commenCount >= 44, "Comment count = " + commenCount);
            Assert.IsTrue(commentsAttachments >= 12, "Total attachments = " + commentsAttachments);
            Assert.IsTrue(sumCommentRating > 50, "Sum comment rating = " + sumCommentRating);
            Assert.IsTrue(notRootCommentCount > 30, "Not root comment count = " + notRootCommentCount);
            Assert.IsTrue(commenCount - notRootCommentCount > 10, "Root comment count = " + (commenCount - notRootCommentCount));
        }

        [Test]
        public void TestPost1323757()
        {
			ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator(new TestModule()));
            var parser = new ReactorParser();

            bool wasBegin = false;
            bool wasInfo = false;
            int commenCount = 0;
            int commentsAttachments = 0;
            var comIds = new HashSet<string>();
            float sumCommentRating = 0;
            int notRootCommentCount = 0;

            parser.ExtractPost("1323757",
                s =>
                {
                    // TODO
                    if (s.State == PostExportState.ExportState.Begin)
                    {
                        wasBegin = true;
                    }
                    else if (s.State == PostExportState.ExportState.Info)
                    {
                        wasInfo = true;

                        Assert.AreEqual("Dendy-A-", s.userName);
                        Assert.AreEqual("http://img0.joyreactor.cc/pics/avatar/user/47722", s.userImage);
                        Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-1240714.jpeg", s.image);
                        Assert.AreEqual(195, s.imageWidth);
                        Assert.AreEqual(208, s.imageHeight);
                    }
                    else if (s.State == PostExportState.ExportState.Comment)
                    {
                        Assert.IsNotNull(s.Comment);
                        commenCount++;

                        sumCommentRating += s.Comment.rating;

                        Assert.IsTrue(s.Comment.Created <= 1577836800000L && s.Comment.Created >= 1262304000000L, "Comment created = " + s.Comment.Created);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.id, "\\d+"), "Comment id = " + s.Comment.id);
                        comIds.Add(s.Comment.id);
						if (s.Comment.ParentIds != null) {
							foreach (var c in s.Comment.ParentIds) {
								Assert.IsTrue(comIds.Contains(c), "Comment parent id = " + c);
								notRootCommentCount++;
							}
						}

                        var t = s.Comment.text;
                        Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Comment.text);
                        TestCommentTextNotContainsTag(s.Comment.text);

                        Assert.NotNull(s.Comment.UserName);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.UserName, "[\\w\\d_]+"), "Comment user name = " + s.Comment.UserName);
                        Assert.IsNotNull(s.Comment.UserImage);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.UserImage, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.Comment.UserImage);

                        Assert.IsNotNull(s.Comment.Attachments);
                        commentsAttachments += s.Comment.Attachments.Length;
                        foreach (var z in s.Comment.Attachments)
                        {
                            Assert.IsTrue(Regex.IsMatch(z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Comment.id + ", url = " + z.Image);
                        }
                    }
                });

            Assert.IsTrue(wasBegin);
            Assert.IsTrue(wasInfo);
            Assert.IsTrue(commenCount >= 2520, "Comment count = " + commenCount);
            Assert.IsTrue(commentsAttachments >= 577, "Total attachments = " + commentsAttachments);
            Assert.IsTrue(sumCommentRating > 1500, "Sum comment rating = " + sumCommentRating);
            Assert.IsTrue(notRootCommentCount > 2000, "Not root comment count = " + notRootCommentCount);
            Assert.IsTrue(commenCount - notRootCommentCount > 500, "Root comment count = " + (commenCount - notRootCommentCount));
        }

        [Test]
        public void TestPost1382511()
        {
			ServiceLocator.SetLocatorProvider(() => new DefaultServiceLocator(new TestModule()));
            var parser = new ReactorParser();

            bool wasBegin = false;
            bool wasInfo = false;
            int commenCount = 0;
            int commentsAttachments = 0;
            var comIds = new HashSet<string>();
            float sumCommentRating = 0;
            int notRootCommentCount = 0;
            var commentTexts = new HashSet<string>();

            parser.ExtractPost("1382511",
                s =>
                {
                    // TODO
                    if (s.State == PostExportState.ExportState.Begin) {
                        wasBegin = true;
                    } else if (s.State == PostExportState.ExportState.Info) {
                        wasInfo = true;

                        Assert.AreEqual("Mishvanda", s.userName);
                        Assert.AreEqual("http://img0.joyreactor.cc/pics/avatar/user/145422", s.userImage);
                        Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-1316599.gif", s.image);
                        Assert.AreEqual(600, s.imageWidth);
                        Assert.AreEqual(750, s.imageHeight);
                    } else if (s.State == PostExportState.ExportState.Comment) {
                        Assert.IsNotNull(s.Comment);
                        commenCount++;

                        sumCommentRating += s.Comment.rating;

                        Assert.IsTrue(s.Comment.Created <= 1577836800000L && s.Comment.Created >= 1262304000000L, "Comment created = " + s.Comment.Created);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.id, "\\d+"), "Comment id = " + s.Comment.id);
                        comIds.Add(s.Comment.id);
						if (s.Comment.ParentIds != null) {
							foreach (var c in s.Comment.ParentIds) {
								Assert.IsTrue(comIds.Contains(c), "Comment parent id = " + c);
								notRootCommentCount++;
							}
						}

                        var t = s.Comment.text;
                        Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Comment.text);
                        TestCommentTextNotContainsTag(s.Comment.text);

                        Assert.NotNull(s.Comment.UserName);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.UserName, "[\\w\\d_]+"), "Comment user name = " + s.Comment.UserName);
                        Assert.IsNotNull(s.Comment.UserImage);
                        Assert.IsTrue(Regex.IsMatch(s.Comment.UserImage, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.Comment.UserImage);

                        Assert.IsNotNull(s.Comment.Attachments);
                        commentsAttachments += s.Comment.Attachments.Length;
                        foreach (var z in s.Comment.Attachments) {
                            Assert.IsTrue(Regex.IsMatch(z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Comment.id + ", url = " + z.Image);
                        }

                        if (!string.IsNullOrEmpty(s.Comment.text)) {
                            Assert.IsFalse(commentTexts.Contains(s.Comment.text), "Text = " + s.Comment.text);
                            commentTexts.Add(s.Comment.text);
                        }
                    }
                });

            Assert.IsTrue(wasBegin);
            Assert.IsTrue(wasInfo);
            Assert.IsTrue(commenCount >= 38, "Comment count = " + commenCount);
            Assert.IsTrue(commentsAttachments >= 7, "Total attachments = " + commentsAttachments);
            Assert.IsTrue(sumCommentRating >= 62, "Sum comment rating = " + sumCommentRating);
            Assert.IsTrue(notRootCommentCount >= 22, "Not root comment count = " + notRootCommentCount);
            Assert.IsTrue(commenCount - notRootCommentCount >= 16, "Root comment count = " + (commenCount - notRootCommentCount));
        }

        private static void TestCommentTextNotContainsTag(string text) {
            var t = text == null ? null : text.ToLower();
            Assert.IsFalse(t != null && new string[] {
				"<br>", "<br />", "<p>", "<a ", "</a>"
			}.Any(a => t.Contains(a)), "Comment = " + text);
        }
    }
}