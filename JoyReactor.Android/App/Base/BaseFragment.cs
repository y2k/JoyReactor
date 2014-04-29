using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.Support.V4.App;
using Autofac;
using Android.OS;
using JoyReactor.Core.Model.Inject;

namespace JoyReactor.Android.App.Base
{
	public class BaseFragment : Fragment
	{
		public override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);
		}
	}
}