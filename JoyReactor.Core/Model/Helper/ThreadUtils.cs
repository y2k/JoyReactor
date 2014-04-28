using System;
using System.Threading;

namespace JoyReactor.Core.Model.Helper
{
	public class ThreadUtils
	{
		public static void Sleep(int duration) 
		{
			new ManualResetEvent (false).WaitOne(duration);
		}
	}
}