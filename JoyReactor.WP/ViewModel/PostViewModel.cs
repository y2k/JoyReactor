using GalaSoft.MvvmLight;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.ViewModel
{
    public class PostViewModel : ViewModelBase
    {
        public ObservableCollection<ItemPostViewModel> Posts { get; private set; }

        public PostViewModel()
        {
            Posts = new ObservableCollection<ItemPostViewModel>();
            //
        }

        public class ItemPostViewModel : ViewModelBase
        {

        }
    }
}