using System;

namespace JoyReactor.Core.Model.Helper
{
	public static class DateTimeExtensions
	{
		public static long ToUnixTimestamp (this DateTime dateTime)
		{
			return (long)((dateTime - new DateTime (1970, 1, 1).ToLocalTime ()).TotalSeconds);
		}

		public static DateTime DateTimeFromUnixTimestampMs (this long timestamp)
		{
			return new DateTime (1970, 1, 1).ToLocalTime () + TimeSpan.FromMilliseconds (timestamp);
		}

		public static DateTime DateTimeFromUnixTimestamp (this long timestamp)
		{
			return DateTimeFromUnixTimestampMs (1000 * timestamp);
		}
	}
}