using System;
using Autofac;
using JoyReactor.Core.Model.Web;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using Cirrious.MvvmCross.Community.Plugins.Sqlite.Wpf;

namespace JoyReactor.Core.Tests.Inner
{
	public class TestModule : Module
	{
		protected override void Load (ContainerBuilder b)
		{
			b.RegisterType<MockWebDownloader> ().As<IWebDownloader> ();
			b.RegisterInstance (MockSQLiteConnection.Create ()).As<ISQLiteConnection> ();
		}
	}
}