using GalaSoft.MvvmLight;
using System.Linq;
using JoyReactor.Core.Model;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Collections.Generic;
using JoyReactor.Core.Model.DTO;

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

        bool _isBusy;
        public bool IsBusy
        {
            get { return _isBusy; }
            set { Set(ref _isBusy, value); }
        }

        public async Task Initialize(int postId)
        {
            IsBusy = true;

            var post = await new PostModel().GetPostAsync(postId);
            var attachments = await new PostModel().GetPostAttachmentsAsync(postId);

            Comments.ReplaceAll(ConvertToViewModels(await new PostModel().GetCommentsAsync(postId, 0)));

            Image = attachments.Select(s => s.PreviewImageUrl).FirstOrDefault();
            IsBusy = false;
        }

        IEnumerable<CommentViewModel> ConvertToViewModels(IEnumerable<Comment> comments)
        {
            foreach (var s in comments)
                yield return new CommentViewModel();
        }

        public class CommentViewModel : ViewModelBase
        {
            public FixRelayCommand NavigateCommand { get; set; }
        }
    }
}