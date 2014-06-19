using System;

namespace JoyReactor.Core.Model.Helper
{
	public static class HtmlUtils
	{
		public static string HtmlToString(this string s) {
			return s.Replace ("<br>", Environment.NewLine);
		}
	}
}