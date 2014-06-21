using System;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Database
{
	public static class SQLiteConnectionExtensions
	{
		public static T SafeExecuteScalar<T> (this ISQLiteConnection instance, string query, params object[] args) {
			lock (instance) {
				return instance.ExecuteScalar<T> (query, args);
			}
		}

		public static int SafeExecute (this ISQLiteConnection instance, string query, params object[] args) {
			lock (instance) {
				return instance.Execute (query, args);
			}
		}

		public static int SafeInsert (this ISQLiteConnection instance, object obj) {
			lock (instance) {
				return instance.Insert (obj);
			}
		}

		public static IEnumerable<T> SafeDeferredQuery<T> (this ISQLiteConnection instance, string query, params object[] args) where T : new() {
			lock (instance) {
				return instance.DeferredQuery<T> (query, args);
			}
		}

		public static int SafeUpdate (this ISQLiteConnection instance, object obj) {
			lock (instance) {
				return instance.Update (obj);
			}
		}

		public static List<T> SafeQuery<T> (this ISQLiteConnection instance, string query, params object[] args) where T : new(){
			lock (instance) {
				return instance.Query<T> (query, args);
			}
		}

		public static void SafeRunInTransaction (this ISQLiteConnection instance, Action action) {
			lock (instance) {
				instance.RunInTransaction (action);
			}
		}
	}
}