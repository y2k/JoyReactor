using JoyReactor.Core.Model;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Tests.Helpers;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System;
using System.Text.RegularExpressions;

namespace JoyReactor.Core.Tests
{
	[TestFixture]
	public class Chan4ParserTests
	{
		private SiteApi parser;

		[Test]
		public void TestGetThread572092321 ()
		{
			int attachemnts = 0;
			parser.NewPostInformation += (sender, e) => {
				// TODO

				Assert.AreEqual (1, e.Attachments.Length, "Count = " + e.Attachments.Length);
				var a = e.Attachments [0];
				Assert.IsTrue (Uri.IsWellFormedUriString (a.Image, UriKind.Absolute), "Url = " + a.Image);
				Assert.AreEqual (720, a.Width, "Width = " + a.Width);
				Assert.AreEqual (720, a.Height, "Height = " + a.Height);

				attachemnts += e.Attachments.Length;
			};
			parser.NewComment += (sender, e) => {
				// TODO

				foreach (var a in e.Attachments) {
					Assert.IsTrue (Uri.IsWellFormedUriString (a.Image, UriKind.Absolute), "Url = " + a.Image);
					Assert.IsTrue (a.Width > 0, "Width = " + a.Width);
					Assert.IsTrue (a.Height > 0, "Height = " + a.Height);
				}

				attachemnts += e.Attachments.Length;
			};
			parser.ExtractPost ("b,572092321");

			Assert.AreEqual (90, attachemnts, "Count = " + attachemnts);
		}

		[Test]
		public void TestGetPostsFromB ()
		{
			int actualPostCount = 0;
			parser.NewPost += (sender, e) => {
				Assert.IsNotNull (e);
				actualPostCount++;
				// TODO: дописать тест
				Assert.IsTrue (Regex.IsMatch (e.Id, @"b,\d+"), "Post id = " + e.Id);
			};
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag ("b", ID.TagType.Good, 0);

			Assert.AreEqual (15, actualPostCount);
		}

		[Test]
		public void TestMultiPageLoading ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);

			for (int i = 0; i < 2; i++)
				parser.ExtractTag ("b", ID.TagType.Good, i);
		}

		[SetUp]
		public void SetUp ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new TestModule ()));
			parser = new Chan4Parser ();
		}

		[Test]
		public void Chan4_GetPosts_WSG ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag ("wsg", ID.TagType.Good, 0);
		}
	}
}