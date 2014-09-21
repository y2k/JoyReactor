using System;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Helper
{
	public static class Log
	{
		private static readonly Lazy<ILogger> Logger = new Lazy<ILogger> (() => ServiceLocator.Current.GetInstance<ILogger> ());

		public static void Message(string message) {
			Logger.Value.Print (message);
		}

		public static void Error(Exception e) {
			Logger.Value.Print ("" + e);
		}

		public interface ILogger {

			void Print(string message);
		}

		internal class DumpLogger : ILogger {

			public void Print (string message)
			{
			}
		}
	}
}