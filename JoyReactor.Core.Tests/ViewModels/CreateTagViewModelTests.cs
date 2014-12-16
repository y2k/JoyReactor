using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using SQLite.Net;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class CreateTagViewModelTests
    {
        CreateTagViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            var provider = new DefaultServiceLocator(new TestModule());
            ServiceLocator.SetLocatorProvider(() => provider);
            viewmodel = new CreateTagViewModel();
        }

        [Test]
        public void NotValidInputTest()
        {
            Assert.IsFalse(viewmodel.NameError);
            viewmodel.CreateCommand.Execute(null);
            Assert.IsTrue(viewmodel.NameError);

            viewmodel.Name = null;
            viewmodel.CreateCommand.Execute(null);
            Assert.IsTrue(viewmodel.NameError);

            viewmodel.Name = "";
            viewmodel.CreateCommand.Execute(null);
            Assert.IsTrue(viewmodel.NameError);

            viewmodel.Name = "            ";
            viewmodel.CreateCommand.Execute(null);
            Assert.IsTrue(viewmodel.NameError);
        }

        [Test]
        public async Task CreateCommandTest()
        {
            viewmodel.Name = "      Test      ";
            viewmodel.CreateCommand.Execute(null);
            await Task.Delay(100);
            Assert.IsFalse(viewmodel.NameError);
            Assert.AreEqual("Test", viewmodel.Name);

            var count = ServiceLocator.Current.GetInstance<SQLiteConnection>().
                ExecuteScalar<int>("SELECT COUNT(*) FROM tags WHERE TagId = ?", ID.Factory.NewTag("test").SerializeToString());
            Assert.IsTrue(count == 1);
        }
    }
}