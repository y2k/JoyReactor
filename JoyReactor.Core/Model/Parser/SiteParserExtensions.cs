using System.Threading.Tasks;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Parser
{
	public static class SiteParserExtensions
	{
		public static Task<ProfileExport> ProfileAsync (this SiteParser parser, string username)
		{
			return Task.Run (() => parser.Profile (username));
		}

		public static Task<IDictionary<string, string>> LoginAsync (this SiteParser parser, string username, string password)
		{
			return Task.Run (() => parser.Login (username, password));
		}
	}
}