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
        ID testId;

        [SetUp]
        public void SetUp()
        {
            var provider = new DefaultServiceLocator(new TestModule());
            ServiceLocator.SetLocatorProvider(() => provider);
            vm = new FeedViewModel();
        }

        [Test]
        public async Task ApplyTest()
        {
            testId = ID.Factory.NewTag("песочница");
            await LoadFirstPage();

            TestExtensions.SetFakeSite("http://joyreactor.cc/tag/песочница", "joyreactor_pesochnica_2.html");

            Assert.IsFalse(vm.HasNewItems);
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);

            vm.ChangeCurrentListIdCommand.Execute(testId);

            Assert.IsTrue(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);

            await Task.Delay(DefaultDelay);

            Assert.IsTrue(vm.HasNewItems);
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);
            CollectionAssert.AreEqual(GenerateList(10, 1), vm.Posts, new TypeComparer());
        }

        [Test]
        public async Task PagingTest()
        {
            testId = ID.Factory.New(ID.IdConst.ReactorGood);
            await LoadFirstPage();
            await LoadNextPage(11);
            await LoadNextPage(21);
        }

        async Task LoadFirstPage()
        {
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(0, vm.Posts.Count);

            vm.ChangeCurrentListIdCommand.Execute(testId);

            Assert.IsTrue(vm.IsBusy);
            Assert.AreEqual(0, vm.Posts.Count);

            await Task.Delay(DefaultDelay);

            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);
            CollectionAssert.AreEqual(GenerateList(10, 1), vm.Posts, new TypeComparer());
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
            CollectionAssert.AreEqual(GenerateList(initCount + 10 - 1, 1), vm.Posts, new TypeComparer());
        }

        private FeedViewModel.DividerViewModel GetDivider()
        {
            return vm.Posts.OfType<FeedViewModel.DividerViewModel>().First();
        }

        private static IEnumerable GenerateList(int preCount, int divCount)
        {
            for (int i = 0; i < preCount; i++)
                yield return new FeedViewModel.ContentViewModel(null);
            for (int i = 0; i < divCount; i++)
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