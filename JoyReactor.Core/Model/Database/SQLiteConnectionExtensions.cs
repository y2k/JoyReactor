using System;
using System.Collections.Generic;
using System.Collections;
using SQLite.Net;

namespace JoyReactor.Core.Model.Database
{
	public static class SQLiteConnectionExtensions
	{
		public static int SafeInsertAll (this SQLiteConnection instance, IEnumerable objects)
		{
			lock (instance) {
				return instance.InsertAll (objects);
			}
		}

		public static T SafeExecuteScalar<T> (this SQLiteConnection instance, string query, params object[] args)
		{
			lock (instance) {
				return instance.ExecuteScalar<T> (query, args);
			}
		}

		public static int SafeExecute (this SQLiteConnection instance, string query, params object[] args)
		{
			lock (instance) {
				return instance.Execute (query, args);
			}
		}

		public static int SafeInsert (this SQLiteConnection instance, object obj)
		{
			lock (instance) {
				return instance.Insert (obj);
			}
		}

		public static IEnumerable<T> SafeDeferredQuery<T> (this SQLiteConnection instance, string query, params object[] args) where T : new()
		{
			lock (instance) {
				return instance.DeferredQuery<T> (query, args);
			}
		}

		public static int SafeUpdate (this SQLiteConnection instance, object obj)
		{
			lock (instance) {
				return instance.Update (obj);
			}
		}

		public static List<T> SafeQuery<T> (this SQLiteConnection instance, string query, params object[] args) where T : new()
		{
			lock (instance) {
				return instance.Query<T> (query, args);
			}
		}

		public static void SafeRunInTransaction (this SQLiteConnection instance, Action action)
		{
			lock (instance) {
				instance.RunInTransaction (action);
			}
		}
	}
}