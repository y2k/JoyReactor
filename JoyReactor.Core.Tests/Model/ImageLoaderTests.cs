using JoyReactor.Core.Model;
using Microsoft.Practices.ServiceLocation;
using NUnit.Framework;
using System;
using System.Drawing;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.Model
{
    [TestFixture]
    public class ImageLoaderTests
    {
        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
        }

        [Test]
        [Ignore]
        public async Task Test()
        {
            // FIXME:
//            var model = ServiceLocator.Current.GetInstance<ImageModel>();
//
//            object result = null;
//            model.Load(new object(), new Uri("http://upload.wikimedia.org/wikipedia/meta/0/08/Wikipedia-logo-v2_1x.png"), 100, s => result = s);
//
//            await Task.Delay(1000);
//
//            Assert.IsNotNull(result);
//            var image = (Bitmap)result;
//            Assert.AreEqual(100, image.Width);
//            Assert.AreEqual(91, image.Height);
        }
        [Ignore]

        [Test]
        [Timeout(2000)]
        public async Task LoadTest()
        {
            // FIXME:
//            var loader = new ImageLoader(new Uri("http://upload.wikimedia.org/wikipedia/meta/0/08/Wikipedia-logo-v2_1x.png"), 100);
//            var image = await loader.Load<Bitmap>();
//
//            Assert.IsNotNull(image);
//            Assert.AreEqual(100, image.Width);
//            Assert.AreEqual(91, image.Height);
        }
    }
}