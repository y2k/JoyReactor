using System;
using HtmlAgilityPack;

namespace JoyReactor.Core.Model.Helper
{
	public static class HtmlNodeExtensions
	{
		public static string GetClass(this HtmlNode node)
		{
			return node.Attributes ["class"].Value;
		}
	}
}