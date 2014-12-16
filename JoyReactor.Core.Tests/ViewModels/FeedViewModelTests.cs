using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.ViewModels;
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
            TestExtensions.SetUp();
            vm = new FeedViewModel();
        }

        [Test]
        public async Task LoadNewDataOverOldTest()
        {
            testId = ID.Factory.NewTag("песочница");
            await LoadFirstPage();
            await LoadNewFirstPageOverOld();
            await ApplyNewItems();
        }

        async Task LoadNewFirstPageOverOld()
        {
            Assert.IsFalse(vm.HasNewItems);
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);
            AssetDatabaseItems(0, 0, 10);

            TestExtensions.SetFakeSite("http://joyreactor.cc/tag/песочница", "joyreactor_pesochnica_2.html");
            vm.ChangeCurrentListIdCommand.Execute(testId);

            Assert.IsTrue(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);

            await Task.Delay(DefaultDelay);

            Assert.IsTrue(vm.HasNewItems);
            Assert.IsFalse(vm.IsBusy);
            Assert.AreEqual(11, vm.Posts.Count);
            AssetDatabaseItems(0, 8, 10);
            AssertPostOrder(10, 1, 0);
        }

        async Task ApplyNewItems()
        {
            Assert.IsTrue(vm.HasNewItems);

            vm.ApplyCommand.Execute(null);
            await Task.Delay(DefaultDelay);

            Assert.IsFalse(vm.HasNewItems);
            Assert.AreEqual(19, vm.Posts.Count);
            AssertPostOrder(10, 1, 8);
        }

        void AssetDatabaseItems(int oldCount, int pendingCount, int actualCount)
        {
            //var db = TestExtensions.GetDatabase();
            //var sql = "SELECT COUNT(*) FROM tag_post WHERE TagId IN (SELECT Id FROM tags WHERE TagId = ?) AND Status = ?";

            //Assert.AreEqual(actualCount, db.ExecuteScalar<int>(sql, testId.SerializeToString(), TagPost.StatusActual));
            //Assert.AreEqual(pendingCount, db.ExecuteScalar<int>(sql, testId.SerializeToString(), TagPost.StatusPending));
            //Assert.AreEqual(oldCount, db.ExecuteScalar<int>(sql, testId.SerializeToString(), TagPost.StatusOld));
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
            AssertPostOrder(10, 1);
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
            AssertPostOrder(initCount + 10 - 1, 1);
        }

        void AssertPostOrder(int beforeDivider, int dividerCount, int afterDivider = 0)
        {
            CollectionAssert.AreEqual(GenerateList(beforeDivider, dividerCount, afterDivider), vm.Posts, new TypeComparer());
        }

        FeedViewModel.DividerViewModel GetDivider()
        {
            return vm.Posts.OfType<FeedViewModel.DividerViewModel>().First();
        }

        static IEnumerable GenerateList(int preCount, int divCount, int afterCount)
        {
            for (int i = 0; i < preCount; i++)
                yield return new FeedViewModel.ContentViewModel(null);
            for (int i = 0; i < divCount; i++)
                yield return new FeedViewModel.DividerViewModel(() => { });
            for (int i = 0; i < afterCount; i++)
                yield return new FeedViewModel.ContentViewModel(null);
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