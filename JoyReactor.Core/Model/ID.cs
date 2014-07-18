using System;
using JoyReactor.Core.Model.Parser;
using System.Collections.Generic;

namespace JoyReactor.Core
{
	public class ID
	{
		public enum IdConst { ReactorGood, ReactorBest, ReactorAll, ReactorFavorite }

		private const char Divider = '\u0000';
		private static readonly Dictionary<IdConst, ID> Consts = new Dictionary<IdConst, ID>() {
			{ IdConst.ReactorGood, new ID { Site = SiteParser.JoyReactor, Type = TagType.Good } },
			{ IdConst.ReactorBest, new ID { Site = SiteParser.JoyReactor, Type = TagType.Best } },
			{ IdConst.ReactorAll, new ID { Site = SiteParser.JoyReactor, Type = TagType.All } },
			{ IdConst.ReactorFavorite, new ID { Site = SiteParser.JoyReactor, Type = TagType.Favorite } }
		};

		internal SiteParser Site { get; set; }
		internal TagType Type { get; set; }
		internal string Tag { get; set; }

		public enum SiteParser { JoyReactor, Chan4, Chan7, Chan2 }
		public enum TagType { Best, Good, All, Favorite }

		public string SerializeToString () {
			return "" + Site + Divider + Type + Divider + Tag;
		}

		public static ID DeserializeFromString(string value) {
			var p = value.Split (Divider);
			return new ID {
				Site = (SiteParser)Enum.Parse (typeof(SiteParser), p [0]), 
				Type = (TagType)Enum.Parse (typeof(TagType), p [1]), 
				Tag = p [2]
			};
		}

		public static ID Parser(string id) {
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
            public static ID New(SiteParser site, string tag)
            {
                return new ID { Site = site, Type = TagType.Good, Tag = tag };
            }

            public static ID New(IdConst c)
            {
				return Consts [c];
			}

			public static ID NewTag(string tag) {
				return new ID { Site = SiteParser.JoyReactor, Type = TagType.Good, Tag = tag };
			}

			public static ID NewFavoriteForUser(string username) {
				return new ID { Site = SiteParser.JoyReactor, Type = TagType.Good, Tag = username };
			}
		}
	}
}