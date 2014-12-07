using Autofac;
using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using JoyReactor.Core.Model.Web;
using Refractored.Xam.Settings.Abstractions;
using JoyReactor.Core.Tests.Xam.Pluging.Settings;

namespace JoyReactor.Core.Tests.Inner
{
	public class TestModule : Module
	{
		protected override void Load (ContainerBuilder b)
		{
			b.RegisterType<MockWebDownloader> ().As<IWebDownloader> ().SingleInstance ();
			b.RegisterInstance (MockSQLiteConnection.Create ()).As<ISQLiteConnection> ();
			b.RegisterInstance (new MockSettings ()).As<ISettings> ();
		}
	}
}