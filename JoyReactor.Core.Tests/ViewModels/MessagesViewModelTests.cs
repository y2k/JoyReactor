using NUnit.Framework;
using System.Linq;
using JoyReactor.Core.ViewModels;
using GalaSoft.MvvmLight.Messaging;
using System.Threading.Tasks;
using JoyReactor.Core.Model.Messages;
using JoyReactor.Core.Model.DTO;
using System;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class MessagesViewModelTests
    {
        MessagesViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new MessagesViewModel();
        }

        public void TearDown()
        {
            viewmodel.Cleanup();
        }

        [Test]
        [Timeout(1000)]
        public async void TestUserY2k()
        {
            await AssertUser("_y2k");
            Assert.AreEqual(27, viewmodel.Messages.Count);
            Assert.AreEqual(15, viewmodel.Messages.Count(s => s.Mode == PrivateMessage.ModeInbox));
            Assert.AreEqual(12, viewmodel.Messages.Count(s => s.Mode == PrivateMessage.ModeOutbox));

            Assert.AreEqual(
                "Американский психолог поставил мир с головы на ноги (см. илл.).", 
                viewmodel.Messages.First().Message);
            Assert.AreEqual(new DateTime(2015, 1, 10, 12, 58, 56), viewmodel.Messages.First().Created);
            Assert.AreEqual(PrivateMessage.ModeOutbox, viewmodel.Messages.First().Mode);

            Assert.AreEqual(
                "1946 — первое заседание Генеральной Ассамблеи ООН в Лондоне",
                viewmodel.Messages.Last().Message);
            Assert.AreEqual(new DateTime(2015, 1, 10, 13, 04, 25), viewmodel.Messages.Last().Created);
            Assert.AreEqual(PrivateMessage.ModeInbox, viewmodel.Messages.Last().Mode);
        }

        [Test]
        [Timeout(1000)]
        public async void TestNotExistsUser()
        {
            await AssertUser("not-exists-user");
            Assert.AreEqual(0, viewmodel.Messages.Count);
        }

        async Task AssertUser(string username)
        {
            await new MessageFetcher().FetchAsync();
            Assert.IsFalse(viewmodel.IsBusy);
            Messenger.Default.Send(new MessagesViewModel.SelectThreadMessage { Username = username });
            Assert.IsTrue(viewmodel.IsBusy);
            await Task.Delay(200);
            Assert.IsFalse(viewmodel.IsBusy);
        }
    }
}