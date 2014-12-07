using System;
using System.Collections.Generic;
using Refractored.Xam.Settings;
using Refractored.Xam.Settings.Abstractions;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Parser
{
	public class ReactorDomainDetector
	{
		static Dictionary<TagType, string> DomainsForTypes = new Dictionary<TagType, string> {
			[TagType.Normal] = "joyreactor.cc",
			[TagType.Secret] = "pornreactor.cc",
		};

		ISettings settings = ServiceLocator.Current.GetInstance<ISettings>();

		public object GetDomainForType (TagType type)
		{
			return DomainsForTypes [type];
		}

		public string GetDomainForTag (string tag)
		{
			var isSecret = settings.GetValueOrDefault (CreateKey (tag), false);
			var tagType = isSecret ? TagType.Secret : TagType.Normal;
			return DomainsForTypes [tagType];
		}

		public void SetTagType(string tag, TagType type) {
			if (type == TagType.Secret)
				settings.AddOrUpdateValue (CreateKey (tag), true);
		}

		string CreateKey (string tag)
		{
			return "reactor.domain.detector.1." + tag;
		}

		public enum TagType
		{
			Normal,
			Secret
		}
	}
}