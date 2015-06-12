using NUnit.Framework;
using System.Linq;

namespace JoyReactor.Core.Tests
{
    [TestFixture]
	public class ReactorParserTests
	{
		//JoyReactorProvider parser;
		//JoyReactorProvider.IStorage mockStorage;
		//JoyReactorProvider.IListStorage listStorage;

		[SetUp]
		public void SetUp ()
		{
			//listStorage = Mock.Of<JoyReactorProvider.IListStorage> ();
			//mockStorage = Mock.Of<JoyReactorProvider.IStorage> ();
			//TestExtensions.BeginInjection ()
			//	.Add<JoyReactorProvider.IStorage> (mockStorage)
			//	.Add<JoyReactorProvider.IListStorage> (listStorage)
			//	.Commit ();

			//parser = JoyReactorProvider.Create ();
		}

		[Test]
		[Ignore]
		public void TestProfileOfUserEksFoxX ()
		{
			// FIXME:
//            var profile = await parser.ProfileAsync("eksFox_X");
//
//            Assert.AreEqual("eksFox_X", profile.UserName);
//            Assert.AreEqual(new Uri("http://img0.joyreactor.cc/pics/avatar/user/19639"), profile.UserImage);
//            Assert.AreEqual(671.6f, profile.Rating);
//
//            Assert.AreEqual(4, profile.Stars);
//            Assert.AreEqual(0.4f, profile.NextStarProgress);
//            Assert.AreEqual(13, profile.Awards.Count);
//
//            foreach (var s in profile.Awards)
//            {
//                Assert.IsTrue(s.Name == s.Name.Trim() && s.Name.Length > 5, "Award name = " + s.Name);
//                Assert.IsTrue(Regex.IsMatch(s.Image, @"http://img0.joyreactor.cc/pics/award/\d+"), "Award image = " + s.Image);
//            }
		}

		[Test]
        [Ignore]
        public async void TestFavoriteUserY2k ()
		{
			//TestExtensions.BeginInjection ().Add<JoyReactorProvider.IStorage> ().Commit ();
			//var listStorage = Mock.Of<JoyReactorProvider.IListStorage> ();
   //         await parser.LoadTagAndPostListAsync (ID.Factory.NewFavoriteForUser ("_y2k"), listStorage, true);
			//Mock.Get (listStorage).Verify (s => s.AddPost (It.IsAny<Post> ()), Times.Exactly (3));
		}

		[Test]
        [Ignore]
		public async void TestFeaturedThreePages ()
		{
//			var id = ID.Factory.New (ID.IdConst.ReactorGood);
//            await parser.LoadTagAndPostListAsync (id, listStorage, true);

//			Mock.Get (mockStorage)
//				.Verify (s => s.UpdateTagInformationAsync (ID.Factory.New (ID.IdConst.ReactorGood), null, 4876, true));

//			// Next page

//			Mock.Get (mockStorage)
//				.Setup (s => s.GetNextPageForTagAsync (id)).Returns (Task.FromResult (4876));

//            await parser.LoadTagAndPostListAsync (id, listStorage, true);

//			Mock.Get (mockStorage)
//				.Verify (s => s.UpdateTagInformationAsync (ID.Factory.New (ID.IdConst.ReactorGood), null, 4875, true));

//			// Next page

//			Mock.Get (mockStorage)
//				.Setup (s => s.GetNextPageForTagAsync (id)).Returns (Task.FromResult (4875));

//            await parser.LoadTagAndPostListAsync (id, listStorage, true);

//			Mock.Get (mockStorage)
//				.Verify (s => s.UpdateTagInformationAsync (ID.Factory.New (ID.IdConst.ReactorGood), null, 4874, true));

////            var postIds = new HashSet<string>();
////            parser.NewPost += (sender, e) =>
////            {
////                Assert.IsNotNull(e);
////                postIds.Add(e.Id);
////            };
////            parser.NewTagInformation += (sender, e) => Assert.AreEqual(4313, e.NextPage);
////            parser.ExtractTag(null, ID.TagType.Good, null);
////            Assert.AreEqual(10, postIds.Count);
////
////            parser = new JoyReactorSiteApi();
////            parser.NewPost += (sender, e) =>
////            {
////                Assert.IsNotNull(e);
////                postIds.Add(e.Id);
////            };
////            parser.NewTagInformation += (sender, e) => Assert.AreEqual(4312, e.NextPage);
////            parser.ExtractTag(null, ID.TagType.Good, 4313);
////            Assert.AreEqual(20, postIds.Count);
////
////            parser = new JoyReactorSiteApi();
////            parser.NewPost += (sender, e) =>
////            {
////                Assert.IsNotNull(e);
////                postIds.Add(e.Id);
////            };
////            parser.NewTagInformation += (sender, e) => Assert.AreEqual(4311, e.NextPage);
////            parser.ExtractTag(null, ID.TagType.Good, 4312);
////            Assert.AreEqual(30, postIds.Count);
		}

		[Test]
		[Ignore] // FIXME:
        public void TestFeatured ()
		{
//            parser.NewPost += (sender, e) => Assert.IsNotNull(e);
//            parser.NewTagInformation += (sender, e) => Assert.IsNotNull(e);
//            parser.ExtractTag(null, ID.TagType.Good, null);
		}

		[Test]
		[Ignore] // FIXME:
        public void TestComics ()
		{
//            int linkedTagCount = 0;
//
//            parser.NewPost += (sender, e) => Assert.IsNotNull(e);
//            parser.NewTagInformation += (sender, e) => Assert.IsNotNull(e);
//            parser.NewLinkedTag += (sender, e) =>
//            {
//                Assert.IsNotNull(e);
//                linkedTagCount++;
//            };
//            parser.ExtractTag("комиксы", ID.TagType.Good, null);
//
//            Assert.True(linkedTagCount > 0, "Linked tags = " + linkedTagCount);
		}

		[Test]
        [Ignore]
        public async void TestPost861529 ()
		{
//			var mockStorage = Mock.Of<JoyReactorProvider.IStorage> ();
//			TestExtensions.BeginInjection ().Add<JoyReactorProvider.IStorage> (mockStorage).Commit ();

//			Mock.Get (mockStorage)
//				.Setup (mock => mock.SaveNewOrUpdatePostAsync (It.IsAny<Post> ()))
//				.Returns (Task.FromResult ((object)null));

//			int commenCount = 0, notRootCommentCount = 0, commentsAttachments = 0;
//			float sumCommentRating = 0;
//			var comIds = new HashSet<int> ();
//			Mock.Get (mockStorage)
//				.Setup (mock => mock.SaveNewPostCommentAsync (It.IsAny<string> (), It.IsAny<int> (), It.IsAny<Comment> (), It.IsAny<string[]> ()))
//				.Callback<string, int, Comment, string[]> (
//				(postId, parentId, s, attachments) => {
//					Assert.IsNotNull (s);
//					commenCount++;
//					sumCommentRating += s.Rating;
						
//					Assert.IsTrue (s.Created <= 1577836800000L && s.Created >= 1262304000000L, "Comment created = " + s.Created);
//					comIds.Add (s.Id);
//					if (s.ParentCommentId != 0) {
//						Assert.IsTrue (comIds.Contains (s.ParentCommentId), "Comment parent id = " + s.ParentCommentId);
//						notRootCommentCount++;
//					}
						
//					var t = s.Text;
//					Assert.IsTrue (t == null || t == t.Trim (), "Comment text = " + s.Text);
//					TestCommentTextNotContainsTag (s.Text);
						
//					Assert.NotNull (s.UserName);
//					Assert.IsTrue (Regex.IsMatch (s.UserName, "[\\w\\d_]+"), "Comment user name = " + s.UserName);
//					Assert.IsNotNull (s.UserImage);
//					Assert.IsTrue (Regex.IsMatch (s.UserImage, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.UserImage);
						
//					Assert.IsNotNull (s.Attachments);
//					commentsAttachments += s.Attachments.Count;
//					foreach (var z in s.Attachments) {
//						Assert.IsTrue (Regex.IsMatch (z, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z);
//					}
//				})
//				.Returns (Task.FromResult ((object)null));

//			await JoyReactorProvider.Create ().LoadPostAsync ("861529");

//			var expectedPost = new Post ();
//			expectedPost.UserName = "mr.viridis";
//			expectedPost.UserImage = "http://img0.joyreactor.cc/images/default_avatar.jpeg";
//			expectedPost.Created = 1372924750000;

//			Mock.Get (mockStorage).Verify (mock => mock.SaveNewOrUpdatePostAsync (expectedPost));
//			Assert.AreEqual (44, commenCount);

////            bool wasInfo = false;
////            int commenCount = 0;
////            int commentsAttachments = 0;
////            var comIds = new HashSet<string>();
////            float sumCommentRating = 0;
////            int notRootCommentCount = 0;
////
////            parser.NewPostInformation += (sender, s) =>
////            {
////                wasInfo = true;
////
////                Assert.AreEqual("mr.viridis", s.User.Name);
////                Assert.AreEqual("http://img0.joyreactor.com/pics/post/-770859.jpeg", s.Attachments[0].Image);
////                Assert.AreEqual(551, s.Attachments[0].Width);
////                Assert.AreEqual(817, s.Attachments[0].Height);
////                Assert.AreEqual("http://img0.joyreactor.cc/images/default_avatar.jpeg", s.User.Avatar);
////            };
////            parser.NewComment += (sender, s) =>
////            {
////                Assert.IsNotNull(s);
////                commenCount++;
////
////                sumCommentRating += s.Rating;
////
////                Assert.IsTrue(
////                    s.Created <= 1577836800000L.DateTimeFromUnixTimestampMs()
////                    && s.Created >= 1262304000000L.DateTimeFromUnixTimestampMs(),
////                    "Comment created = " + s.Created);
////                Assert.IsTrue(Regex.IsMatch(s.Id, "\\d+"), "Comment id = " + s.Id);
////                comIds.Add(s.Id);
////                if (s.ParentIds != null)
////                    foreach (var c in s.ParentIds)
////                    {
////                        Assert.IsTrue(comIds.Contains(c), "Comment parent id = " + c);
////                        notRootCommentCount++;
////                    }
////
////                var t = s.Content;
////                Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Content);
////                TestCommentTextNotContainsTag(s.Content);
////
////                Assert.NotNull(s.User.Name);
////                Assert.IsTrue(Regex.IsMatch(s.User.Name, "[\\w\\d_]+"), "Comment user name = " + s.User.Name);
////                Assert.IsNotNull(s.User.Avatar);
////                Assert.IsTrue(Regex.IsMatch(s.User.Avatar, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.User.Avatar);
////
////                Assert.IsNotNull(s.Attachments);
////                commentsAttachments += s.Attachments.Length;
////                foreach (var z in s.Attachments)
////                {
////                    Assert.IsTrue(Regex.IsMatch(z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z.Image);
////                }
////            };
////            parser.ExtractPost("861529");
////
////            Assert.IsTrue(wasInfo);
////            Assert.IsTrue(commenCount >= 44, "Comment count = " + commenCount);
////            Assert.IsTrue(commentsAttachments >= 12, "Total attachments = " + commentsAttachments);
////            Assert.IsTrue(sumCommentRating > 50, "Sum comment rating = " + sumCommentRating);
////            Assert.IsTrue(notRootCommentCount > 30, "Not root comment count = " + notRootCommentCount);
////            Assert.IsTrue(commenCount - notRootCommentCount > 10, "Root comment count = " + (commenCount - notRootCommentCount));
		}

		[Test]
		[Ignore] // FIXME:
        public void TestPost1323757 ()
		{
//            bool wasInfo = false;
//            int commenCount = 0;
//            int commentsAttachments = 0;
//            var comIds = new HashSet<string>();
//            float sumCommentRating = 0;
//            int notRootCommentCount = 0;
//
//            parser.NewPostInformation += (sender, s) =>
//            {
//                wasInfo = true;
//
//                Assert.AreEqual("Dendy-A-", s.User.Name);
//                Assert.AreEqual("http://img0.joyreactor.cc/pics/avatar/user/47722", s.User.Avatar);
//                Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-1240714.jpeg", s.Attachments[0].Image);
//                Assert.AreEqual(195, s.Attachments[0].Width);
//                Assert.AreEqual(208, s.Attachments[0].Height);
//            };
//            parser.NewComment += (sender, s) =>
//            {
//                Assert.IsNotNull(s);
//                commenCount++;
//
//                sumCommentRating += s.Rating;
//
//                Assert.IsTrue(
//                    s.Created <= 1577836800000L.DateTimeFromUnixTimestampMs()
//                    && s.Created >= 1262304000000L.DateTimeFromUnixTimestampMs(),
//                    "Comment created = " + s.Created);
//                Assert.IsTrue(Regex.IsMatch(s.Id, "\\d+"), "Comment id = " + s.Id);
//                comIds.Add(s.Id);
//                if (s.ParentIds != null)
//                    foreach (var c in s.ParentIds)
//                    {
//                        Assert.IsTrue(comIds.Contains(c), "Comment parent id = " + c);
//                        notRootCommentCount++;
//                    }
//
//                var t = s.Content;
//                Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Content);
//                TestCommentTextNotContainsTag(s.Content);
//
//                Assert.NotNull(s.User.Name);
//                Assert.IsTrue(Regex.IsMatch(s.User.Name, "[\\w\\d_]+"), "Comment user name = " + s.User.Name);
//                Assert.IsNotNull(s.User.Avatar);
//                Assert.IsTrue(Regex.IsMatch(s.User.Avatar, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.User.Avatar);
//
//                Assert.IsNotNull(s.Attachments);
//                commentsAttachments += s.Attachments.Length;
//                foreach (var z in s.Attachments)
//                {
//                    Assert.IsTrue(Regex.IsMatch(z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z.Image);
//                }
//            };
//            parser.ExtractPost("1323757");
//
//            Assert.IsTrue(wasInfo);
//            Assert.IsTrue(commenCount >= 2520, "Comment count = " + commenCount);
//            Assert.IsTrue(commentsAttachments >= 577, "Total attachments = " + commentsAttachments);
//            Assert.IsTrue(sumCommentRating > 1500, "Sum comment rating = " + sumCommentRating);
//            Assert.IsTrue(notRootCommentCount > 2000, "Not root comment count = " + notRootCommentCount);
//            Assert.IsTrue(commenCount - notRootCommentCount > 500, "Root comment count = " + (commenCount - notRootCommentCount));
		}

		[Test]
		[Ignore] // FIXME:
        public void TestPost1382511 ()
		{
//            bool wasInfo = false;
//            int commenCount = 0;
//            int commentsAttachments = 0;
//            var comIds = new HashSet<string>();
//            float sumCommentRating = 0;
//            int notRootCommentCount = 0;
//            var commentTexts = new HashSet<string>();
//
//            parser.NewPostInformation += (sender, s) =>
//            {
//                Assert.AreEqual("Mishvanda", s.User.Name);
//                Assert.AreEqual("http://img0.joyreactor.cc/pics/avatar/user/145422", s.User.Avatar);
//                Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-1316599.gif", s.Attachments[0].Image);
//                Assert.AreEqual(600, s.Attachments[0].Width);
//                Assert.AreEqual(750, s.Attachments[0].Height);
//            };
//            parser.NewComment += (sender, s) =>
//            {
//                Assert.IsNotNull(s);
//                commenCount++;
//
//                sumCommentRating += s.Rating;
//
//                Assert.IsTrue(
//                    s.Created <= 1577836800000L.DateTimeFromUnixTimestampMs()
//                    && s.Created >= 1262304000000L.DateTimeFromUnixTimestampMs(),
//                    "Comment created = " + s.Created);
//                Assert.IsTrue(Regex.IsMatch(s.Id, "\\d+"), "Comment id = " + s.Id);
//                comIds.Add(s.Id);
//                if (s.ParentIds != null)
//                    foreach (var c in s.ParentIds)
//                    {
//                        Assert.IsTrue(comIds.Contains(c), "Comment parent id = " + c);
//                        notRootCommentCount++;
//                    }
//
//                var t = s.Content;
//                Assert.IsTrue(t == null || t == t.Trim(), "Comment text = " + s.Content);
//                TestCommentTextNotContainsTag(s.Content);
//
//                Assert.NotNull(s.User.Name);
//                Assert.IsTrue(Regex.IsMatch(s.User.Name, "[\\w\\d_]+"), "Comment user name = " + s.User.Name);
//                Assert.IsNotNull(s.User.Avatar);
//                Assert.IsTrue(Regex.IsMatch(s.User.Avatar, "http://img\\d+\\.joyreactor\\.cc/pics/avatar/user/\\d+"), s.User.Avatar);
//
//                Assert.IsNotNull(s.Attachments);
//                commentsAttachments += s.Attachments.Length;
//                foreach (var z in s.Attachments)
//                {
//                    Assert.IsTrue(Regex.IsMatch(z.Image, "http://img\\d+\\.joyreactor\\.cc/pics/comment/\\-\\d+\\.(jpeg|png|gif|bmp)"), "Comment id = " + s.Id + ", url = " + z.Image);
//                }
//
//                if (!string.IsNullOrEmpty(s.Content))
//                {
//                    Assert.IsFalse(commentTexts.Contains(s.Content), "Text = " + s.Content);
//                    commentTexts.Add(s.Content);
//                }
//            };
//            parser.ExtractPost("1382511");
//
//            Assert.IsTrue(wasInfo);
//            Assert.IsTrue(commenCount >= 38, "Comment count = " + commenCount);
//            Assert.IsTrue(commentsAttachments >= 7, "Total attachments = " + commentsAttachments);
//            Assert.IsTrue(sumCommentRating >= 62, "Sum comment rating = " + sumCommentRating);
//            Assert.IsTrue(notRootCommentCount >= 22, "Not root comment count = " + notRootCommentCount);
//            Assert.IsTrue(commenCount - notRootCommentCount >= 16, "Root comment count = " + (commenCount - notRootCommentCount));
		}

		static void TestCommentTextNotContainsTag (string text)
		{
			var t = text == null ? null : text.ToLower ();
			Assert.IsFalse (t != null && new [] {
				"<br>", "<br />", "<p>", "<a ", "</a>"
			}.Any (a => t.Contains (a)), "Comment = " + text);
		}
	}
}