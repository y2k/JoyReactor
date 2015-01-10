using System.Threading.Tasks;
using JoyReactor.Core.ViewModels;
using NUnit.Framework;

namespace JoyReactor.Core.Tests.ViewModels
{
	[TestFixture]
	public class MessageThreadsViewModelTests
	{
		MessageThreadsViewModel viewmodel;

		[SetUp]
		public void SetUp()
		{
			TestExtensions.SetUp();
			viewmodel = new MessageThreadsViewModel();
		}

		[Test]
		public async void Test()
		{
			Assert.IsFalse(viewmodel.IsBusy);
			viewmodel.Initialize();
			Assert.IsTrue(viewmodel.IsBusy);

			await Task.Delay(300);

			Assert.IsFalse(viewmodel.IsBusy);
			Assert.AreEqual(1, viewmodel.Threads.Count);
		}
	}
}