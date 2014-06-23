using GalaSoft.MvvmLight;
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

        internal IDictionary<string, string> Args { get; set; }
    }
}
