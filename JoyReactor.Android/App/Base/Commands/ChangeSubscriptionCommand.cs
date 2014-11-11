using System;
using JoyReactor.Core;
using System.Collections.Generic;
using System.Linq;

namespace JoyReactor.Android.App.Base.Commands
{
	public class ChangeSubscriptionCommand
	{
		static readonly Dictionary<object, Action<ID>> Callbacks = new Dictionary<object, Action<ID>>();

		ID id;

		public ChangeSubscriptionCommand (ID id)
		{
			this.id = id;
		}

		public void Execute ()
		{
			Callbacks.Values.ToList().ForEach(s => s(id));
		}

		public static void Register (object token, Action<ID> callback)
		{
			Callbacks.Add (token, callback);
		}

		public static void Unregister (object token)
		{
			Callbacks.Remove (token);
		}
	}
}