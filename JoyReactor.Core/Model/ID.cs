using System;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core
{
	public class ID
	{
		public static readonly ID REACTOR_GOOD = new ID { Site = SiteParser.JoyReactor, Type = TagType.Good };
		public static readonly ID REACTOR_BEST = new ID { Site = SiteParser.JoyReactor, Type = TagType.Best };
		public static readonly ID REACTOR_ALL = new ID { Site = SiteParser.JoyReactor, Type = TagType.All };
		public static readonly ID ReactorFavorite = new ID { Site = SiteParser.JoyReactor, Type = TagType.Favorite };

		public SiteParser Site { get; set; }
		public TagType Type { get; set; }
		public string Tag { get; set; }

		public enum SiteParser { JoyReactor, Chan4, Chan7, Chan2 }
		public enum TagType { Best, Good, All, Favorite }

		public static ID Parser(string id) 
		{
			var p = id.Split ('-');
			return new ID { 
				Site = (SiteParser)Enum.Parse (typeof(SiteParser), p [0]), 
				Type = (TagType)Enum.Parse (typeof(TagType), p [1]), 
				Tag = p [2] };
		}

		public class TagID : ID 
		{
			// TODO Reserver for future
		}

		public class Factory
		{
			public static ID Tag(SiteParser site, TagType type, String name) 
			{
				return new ID { Site = site, Tag = name, Type = type };
			}

			public static ID Reactor(String name)
			{
				return Tag (SiteParser.JoyReactor, TagType.Good, name);
			}
		}
	}
}