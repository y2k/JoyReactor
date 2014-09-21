using System;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Android.Model
{
	public class LogcatLogger : Log.ILogger
	{
		#region ILogger implementation

		public void Print (string message)
		{
			global::Android.Util.Log.Info ("ILogger", message);
		}

		#endregion
	}
}