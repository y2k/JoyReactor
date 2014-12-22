using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.Model.Web.Parser;
using JoyReactor.Core.Tests.Helpers;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System;

namespace JoyReactor.Core.Tests
{
    [TestFixture]
	public class Chan7ParserTests
	{
		private SiteParser parser;

		[SetUp]
		public void SetUp ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator (new TestModule ()));
			parser = new Chan7Parser ();
		}

		[Test]
		public void Parse722687 ()
		{
			bool newPostEntered = false;
			int commentCount = 0;

			parser.NewPostInformation += (sender, e) => {
				Assert.NotNull (e);
				Assert.AreEqual ("Marisa Kirisame", e.User.Name);
				Assert.AreEqual (
					"Dream thread. Post any dreams that you remember. Pic related: my bed.\n\nI was looking at a CD album art that looked like a gray arch and some clouds, or maybe a girl's butt, but probably a grey stone arch and some grey clouds. Señora S., my Spanish teacher, was explaining to us about the artist, and I looked forward and saw a larger image of the painting. There was a tornado in the ocean, and I asked if God was angry. Señora S. said no, it was only a demon. The tornado turned into a storm that generated some orange light. It plunged down into the water and then exploded, and out of it came an aircraft. This aircraft was similar to the Halberd (Kirby, video game) in shape, but mostly it resembled what I imagined Howl's castle (Howl's Moving Castle, movie) to look like when it was first created. It was a great airship with many wings and Howl was there shouting about how great it was to be flying in the airship, and I was there with him. He said to someone who I now assume to be Calcifer that he should have brought another blanket. We were flying low over great mountains now, and then we were upon them, sliding along the ridge, being pulled by a rope. The ridge was sharp. There was no flat area on the ridge, only an edge where the two sides of the mountain came together. We continued along for some time, slowing down and climbing a bit more upward now. I was holding on to the rope, and pulling myself up, and I realized that I was the last in the band of climbers who perhaps resembled the band of dwarfs in The Hobbit, and so I ran. We came to what seemed to be the top of the mountain, because there were bushes and a stone path. We went up this stone path, amid some apprehensions. I was still the last in line, along with some other person, and we heard someone say \"Get out of here!\" and so I ran the other way. I soon decided that I should not abandon my company, and I went back up the stone path, grabbing onto the bushes to pull me along because it is hard to run in a dream, and there were some of our men and some soldiers with brown stone blocks as part of their heads or their hats, I'm not sure. We were lined up along what looked like a runway for a plane, and there might have been a castle in the background, but I was not paying attention to that. It was like a turn-based RPG. I did a combo attack with my spear, and woke up.",
					e.Content);
				Assert.AreEqual (new DateTime (2014, 5, 6, 10, 38, 0), e.Created);

				Assert.AreEqual (1, e.Attachments.Length);
				var a = e.Attachments [0];
				Assert.AreEqual ("https://7chan.org/b/src/139936549850.jpg", a.Image);
				Assert.AreEqual (2048, a.Width);
				Assert.AreEqual (1536, a.Height);

				newPostEntered = true;
			};
			parser.NewComment += (sender, e) => {
				Assert.NotNull (e);

				commentCount++;
			};

			parser.ExtractPost ("b,722687");

			Assert.IsTrue (newPostEntered);
			Assert.AreEqual (131, commentCount);
		}

		[Test]
		public void Chan7_GetPosts_B ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag ("b", ID.TagType.Good, 0);
		}

		[Test]
		public void TestNextPage ()
		{
			for (int i = 0; i < 2; i++) {
				parser.NewPost += (sender, e) => Assert.IsNotNull (e);
				parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
				parser.ExtractTag ("b", ID.TagType.Good, i);
			}
		}

		[Test]
		public void Chan7_GetPosts_GIF ()
		{
			parser.NewPost += (sender, e) => Assert.IsNotNull (e);
			parser.NewTagInformation += (sender, e) => Assert.IsNotNull (e);
			parser.ExtractTag ("gif", ID.TagType.Good, 0);
		}
	}
}