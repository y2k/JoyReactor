using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using HtmlAgilityPack;

namespace JoyReactor.Core.Model.Helper
{
    static class NodeHelper
	{
		public static IEnumerable<HtmlNode> Select (this HtmlNode root, string xpath)
		{
			var m = Regex.Match (xpath, @"^(\w+)\.([\w-]+)$");
			if (m.Success)
				return root.Descendants ()
                .Where (s => s.Name == m.Groups [1].Value && s.Attributes.Any (a => a.Name == "class" && a.Value == m.Groups [2].Value));

			m = Regex.Match (xpath, @"^(\w+)$");
			if (m.Success)
				return root.Descendants ()
				.Where (s => s.Name == m.Groups [1].Value);

			m = Regex.Match (xpath, @"^(\w+)\.(\w+) (\w+)\.(\w+)$");
			if (m.Success)
				return root.Descendants ()
					.Where (s => s.Name == m.Groups [1].Value && s.ContainsClass (m.Groups [2].Value))
	                .SelectMany (s => s.Descendants ())
					.Where (s => s.Name == m.Groups [3].Value && s.ContainsClass (m.Groups [4].Value));

            m = Regex.Match (xpath, @"^([\w-]+)\.([\w-]+) > ([\w-]+)$");
			if (m.Success)
				return root.Descendants ()
                .Where (s => s.Name == m.Groups [1].Value && s.Attributes.Any (a => a.Name == "class" && a.Value.Contains (m.Groups [2].Value)))
                .SelectMany (s => s.ChildNodes)
                .Where (s => s.Name == m.Groups [3].Value);

			m = Regex.Match (xpath, @"^(\w+)\[(\w+)=(\w+)\]$");
			if (m.Success)
				return root.Descendants ()
                .Where (s => s.Name == m.Groups [1].Value && s.Attributes.Any (a => a.Name == m.Groups [2].Value && a.Value == m.Groups [3].Value));

			m = Regex.Match (xpath, @"^(\w+)\.(\w+)\.(\w+)$");
			if (m.Success)
				return root.Descendants ()
					.Where (s => s.Name == m.Groups [1].Value)
					.Where (s => s.Attributes.Any (a => a.Name == "class"))
					.Select (s => new { node = s, cls = s.Attr ("class").Split (' ') })
					.Where (s => s.cls.Contains (m.Groups [2].Value) && s.cls.Contains (m.Groups [3].Value))
					.Select (s => s.node);

			throw new InvalidOperationException ("Can't parse XPATH = " + xpath);
		}

		static bool ContainsClass (this HtmlNode node, string className)
		{
			var classValue = node.Attr ("class");
			return classValue != null && classValue.Split (' ').Any (s => s == className);
		}

		public static string AbsUrl (this HtmlNode node, Uri baseUrl, string attrName)
		{
			return "" + new Uri (baseUrl, node.Attributes [attrName].Value);
		}

		public static string Attr (this HtmlNode node, string attrName)
		{
			return node.GetAttributeValue (attrName, null);
		}

		public static string ShortString (this string s, int maxLength)
		{
			return s == null ? null : (s.Length <= maxLength ? s : s.Substring (0, maxLength) + "…");
		}
	}
}