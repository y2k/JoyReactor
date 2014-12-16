using GalaSoft.MvvmLight;
using System.Collections.ObjectModel;

namespace JoyReactor.Core.ViewModels
{
    public class PostViewModel : ViewModelBase
    {
        public ObservableCollection<CommentViewModel> Comments { get; } = new ObservableCollection<CommentViewModel>();

        string _image;
        public string Image
        {
            get { return _image; }
            set { Set(ref _image, value); }
        }

        public void LoadData(int postId)
        {
            // TODO
        }

        public class CommentViewModel : ViewModelBase
        {
            public FixRelayCommand NavigateCommand { get; set; }
        }
    }
}