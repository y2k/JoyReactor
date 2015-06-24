using System;
using System.Text.RegularExpressions;

namespace JoyReactor.Core.Model.Helper
{
	public static class RegexExtensions
	{
		public static string FirstString(this Regex regex, string text)
		{
			var m = regex.Match (text);
			return m.Success ? m.Groups [1].Value : null;
		}

		public static int FirstInt(this Regex regex, string text)
		{
			var m = regex.Match (text);
			return m.Success ? int.Parse(m.Groups [1].Value) : 0;
		}

		public static long FirstLong(this Regex regex, string text)
		{
			var m = regex.Match (text);
			return m.Success ? long.Parse(m.Groups [1].Value) : 0;
		}

		public static float FirstFloat(this Regex regex, string text, IFormatProvider format)
		{
			var m = regex.Match (text);
			return m.Success ? float.Parse(m.Groups [1].Value, format) : 0;
		}
	}
}