using NUnit.Framework;
using JoyReactor.Core.Model.Parser;

namespace JoyReactor.Core.Tests.Model.Parser
{
    public class Post1962130Test : BaseTest
    {
        [Test]
        public async void Test()
        {
            await JoyReactorProvider.Create().LoadPostAsync("1962130");
        }
    }
}