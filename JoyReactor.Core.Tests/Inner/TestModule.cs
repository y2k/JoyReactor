using Autofac;
using JoyReactor.Core.Model.Web;
using JoyReactor.Core.Tests.Xam.Pluging.Settings;
using Refractored.Xam.Settings.Abstractions;
using SQLite.Net;

namespace JoyReactor.Core.Tests.Inner
{
    public class TestModule : Module
	{
		protected override void Load (ContainerBuilder b)
		{
			b.RegisterType<MockWebDownloader> ().As<IWebDownloader> ().SingleInstance ();
			b.RegisterInstance (MockSQLiteConnection.Create ()).As<SQLiteConnection> ();
			b.RegisterInstance (new MockSettings ()).As<ISettings> ();
		}
	}
}