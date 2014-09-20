using HtmlAgilityPack;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace JoyReactor.Core.Model.Web.Parser
{
    internal static class NodeHelper
    {
        public static IEnumerable<HtmlNode> Select(this HtmlNode root, string xpath)
        {
            var m = Regex.Match(xpath, @"^(\w+)\.([\w-]+)$");
            if (m.Success) return root.Descendants()
                .Where(s => s.Name == m.Groups[1].Value && s.Attributes.Any(a => a.Name == "class" && a.Value.Contains(m.Groups[2].Value)));

			m = Regex.Match(xpath, @"^(\w+)$");
			if (m.Success) return root.Descendants()
				.Where(s => s.Name == m.Groups[1].Value);

            m = Regex.Match(xpath, "^(\\w+)\\.(\\w+) (\\w+)\\.(\\w+)$");
            if (m.Success) return root.Descendants()
                .Where(s => s.Name == m.Groups[1].Value && s.Attributes.Any(a => a.Name == "class" && a.Value.Contains(m.Groups[2].Value)))
                .SelectMany(s => s.Descendants())
                .Where(s => s.Name == m.Groups[3].Value && s.Attributes.Any(a => a.Name == "class" && a.Value.Contains(m.Groups[4].Value)));

            m = Regex.Match(xpath, "^(\\w+)\\.(\\w+) > (\\w+)$");
            if (m.Success) return root.Descendants()
                .Where(s => s.Name == m.Groups[1].Value && s.Attributes.Any(a => a.Name == "class" && a.Value.Contains(m.Groups[2].Value)))
                .SelectMany(s => s.ChildNodes)
                .Where(s => s.Name == m.Groups[3].Value);

            m = Regex.Match(xpath, "^(\\w+)\\[(\\w+)=(\\w+)\\]$");
            if (m.Success) return root.Descendants()
                .Where(s => s.Name == m.Groups[1].Value && s.Attributes.Any(a => a.Name == m.Groups[2].Value && a.Value == m.Groups[3].Value));

            m = Regex.Match(xpath, "^(\\w+)\\.(\\w+)\\.(\\w+)$");
            if (m.Success) return root.Descendants()
                .Where(s => s.Name == m.Groups[1].Value && s.Attributes.Any(a => a.Name == "class" && a.Value.Contains(m.Groups[2].Value) && a.Value.Contains(m.Groups[3].Value)));

			throw new InvalidOperationException("Can't parse XPATH = " + xpath);
        }

        public static string AbsUrl(this HtmlNode node, Uri baseUrl, string attrName)
        {
            return "" + new Uri(baseUrl, node.Attributes[attrName].Value);
        }

        public static string Attr(this HtmlNode node, string attrName)
        {
            return node.Attributes[attrName].Value;
        }

        public static string ShortString(this string s, int maxLength)
        {
            return s == null ? null : (s.Length <= maxLength ? s : s.Substring(0, maxLength) + "…");
        }

        public static long DateTimeToUnixTimestamp(DateTime dateTime)
        {
            return (long)((dateTime - new DateTime(1970, 1, 1).ToLocalTime()).TotalSeconds);
        }

        public static long ToUnixTimestamp(this DateTime dateTime)
        {
            return (long)((dateTime - new DateTime(1970, 1, 1).ToLocalTime()).TotalSeconds);
        }
    }
}