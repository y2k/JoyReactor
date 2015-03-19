using Refractored.Xam.Settings.Abstractions;
using System.Collections.Generic;

namespace JoyReactor.Core.Tests.Xam.Pluging.Settings
{
    public class MockSettings : ISettings
	{
		Dictionary<string, object> settings = new Dictionary<string, object> ();

		public T GetValueOrDefault<T> (string key, T defaultValue = default(T))
		{
			lock (settings) {
				object result;
				if (settings.TryGetValue (key, out result))
					return (T)result;
				return defaultValue;
			}
		}

		public bool AddOrUpdateValue (string key, object value)
		{
			lock (settings) {
				if (value == null)
					settings.Remove (key);
				else
					settings [key] = value;
			}
			return true;
		}

        public bool AddOrUpdateValue<T>(string key, T value)
        {
            return AddOrUpdateValue(key, (object)value);
        }

        public void Remove(string key)
        {
            settings.Remove(key);
        }

		public void Save ()
		{
			// Ignore
		}
	}
}