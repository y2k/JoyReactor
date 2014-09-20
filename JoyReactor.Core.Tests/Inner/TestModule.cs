using System;
using Autofac;
using JoyReactor.Core.Model.Web;

namespace JoyReactor.Core.Tests.Inner
{
	public class TestModule: Module
	{
		protected override void Load(ContainerBuilder b)
		{
			b.RegisterType<MockWebDownloader>().As<IWebDownloader>();
		}
	}
}