using JoyReactor.Core.Model;
using JoyReactor.Core.Tests.Helpers;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;

namespace JoyReactor.Core.Tests
{
    [TestFixture]
    public class BaseTest
    {
        [SetUp]
        public virtual void SetUp()
        {
            TestExtensions.SetUp();
        }

        [TearDown]
        public virtual void TearDown() {
            TestExtensions.TearDown(this);
        }
    }
}