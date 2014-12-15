using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Tests.Inner;
using JoyReactor.Core.ViewModels;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System.Collections;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    class FeedViewModelTests
    {
        const int DefaultDelay = 400;
        FeedViewModel vm;

        [SetUp]
        public void SetUp()
        {
            var provider = new DefaultServiceLocator(new TestModule());
            ServiceLocator.SetLocatorProvider(() => provider);
            vm = new FeedViewModel();
        }

        [Test]
        public async Task Test()
        {
            await LoadFirstPage();
            await LoadNextPage(11);
            await LoadNextPage(21);
        }

        async Task LoadFirstPage()
        {
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(0, vm.Posts.Count);

            vm.ChangeCurrentListIdCommand.Execute(ID.Factory.New(ID.IdConst.ReactorGood));

            Assert.IsTrue(vm.IsBusy);
            Assert.AreEqual(0, vm.Posts.Count);

            await Task.Delay(DefaultDelay);

            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);
            CollectionAssert.AreEqual(GenerateList(10), vm.Posts, new TypeComparer());
        }

        async Task LoadNextPage(int initCount)
        {
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(initCount, vm.Posts.Count);

            GetDivider().LoadMoreCommand.Execute(null);

            Assert.IsTrue(vm.IsBusy);
            Assert.AreEqual(initCount, vm.Posts.Count);

            await Task.Delay(DefaultDelay);

            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(initCount + 10, vm.Posts.Count);
            CollectionAssert.AreEqual(GenerateList(initCount + 10 - 1), vm.Posts, new TypeComparer());
        }

        private FeedViewModel.DividerViewModel GetDivider()
        {
            return vm.Posts.OfType<FeedViewModel.DividerViewModel>().First();
        }

        private static IEnumerable GenerateList(int count)
        {
            for (int i = 0; i < count; i++)
                yield return new FeedViewModel.ContentViewModel(null);
            yield return new FeedViewModel.DividerViewModel(() => { });
        }

        public class TypeComparer : IComparer
        {
            public int Compare(object x, object y)
            {
                return x.GetType() == y.GetType() ? 0 : 1;
            }
        }
    }
}