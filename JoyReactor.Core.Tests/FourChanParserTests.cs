using NUnit.Framework;
using System;
using Microsoft.Practices.ServiceLocation;
using JoyReactor.Core.Model.Inject;
using JoyReactor.Core.Model.Web.Parser;

namespace JoyReactor.Core.Tests
{
	[TestFixture ()]
	public class FourChanParserTests
	{
		[Test ()]
		public void GetListTest ()
		{
			ServiceLocator.SetLocatorProvider (() => new DefaultServiceLocator ());

			var parser = new FourChanParser ();
			parser.ExtractTagPostCollection (ID.TagType.Good, "b", -1, null, state => {
			});
		}
	}
}