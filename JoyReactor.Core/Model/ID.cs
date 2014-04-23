using System;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core
{
	public class ID
	{
//		public const int SITE_REACTOR = 1;
//		public const int SITE_2CHAN = 2;
//		public const int SITE_4CHAN = 3;
//		public const int SITE_7CHAN = 4;
//
//		public const int TYPE_BEST = 1;
//		public const int TYPE_GOOD = 2;
//		public const int TYPE_ALL = 3;
//		public const int TYPE_FAVORITE = 4;

		public SiteParser Site { get; set; }
		public TagType Type { get; set; }
		public string Tag { get; set; }

		public enum SiteParser { JoyReactor, Chan4, Chan7, Chan2 }
		public enum TagType { Best, Good, All, Favorite }
	}
}