using System;
using Autofac;
using System.Collections.Generic;
using Autofac.Core;

namespace JoyReactor.Core.Model.Inject
{
	public static class InjectServiceExtensions
	{
		public static TService Get<TService> (this IComponentContext context, params Parameter[] parameters)
		{
			return context.Resolve<TService> (parameters);
		}
	}
}