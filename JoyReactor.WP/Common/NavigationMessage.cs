using GalaSoft.MvvmLight;
using JoyReactor.WP.ViewModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel.Channels;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.Common
{
    class NavigationMessage
    {
        internal ViewModelBase ViewModel { get; set; }

        internal Type Target { get; set; }

        internal string TargetName { get; set; }

        internal IDictionary<string, string> Args { get; set; }

        internal NavigationMessage()
        {
            Args = new Dictionary<string, string>();
        }

        public static NavigationMessage Post(int id)
        {
            return new NavigationMessage { Target = typeof(SinglePostViewModel), Args = { { "id", "" + id } } };
        }
    }
}