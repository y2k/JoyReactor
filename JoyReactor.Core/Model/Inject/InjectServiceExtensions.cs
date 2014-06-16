using System;
using Autofac;
using System.Collections.Generic;
using Autofac.Core;
using Microsoft.Practices.ServiceLocation;

namespace JoyReactor.Core.Model.Inject
{
	public static class InjectServiceExtensions
	{
		public static TService Get<TService> (this IComponentContext context, params Parameter[] parameters)
		{
			return context.Resolve<TService> (parameters);
		}

		[Obsolete]
        public static TService Get<TService>(this IServiceLocator context, params Parameter[] parameters)
        {
            return context.GetInstance<TService>();
        }
    }
}