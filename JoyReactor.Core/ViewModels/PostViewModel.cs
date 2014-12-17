using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;

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

            Comments.ReplaceAll(ConvertToViewModels(await new PostModel().GetChildCommentsAsync(postId, 0)));

            Image = attachments.Select(s => s.PreviewImageUrl).FirstOrDefault();
            IsBusy = false;
        }

        IEnumerable<CommentViewModel> ConvertToViewModels(IEnumerable<Comment> comments)
        {
            foreach (var s in comments)
                yield return new CommentViewModel(this, s);
        }

        async void ChangeRootCommen(Comment comment, bool isRoot)
        {
            if (isRoot)
            {
                var comments = await new PostModel().GetCommentsWithSameParentAsync(comment.PostId, comment.Id);
                var parent = await new PostModel().GetParentCommentAsync(comment.PostId, comment.Id);
                Comments.ReplaceAll(ConvertToViewModels(comments));
                if (parent != null)
                    Comments.Insert(0, new CommentViewModel(this, parent) { IsRoot = true });
            }
            else
            {
                var childs = await new PostModel().GetChildCommentsAsync(comment.PostId, comment.Id);
                Comments.ReplaceAll(ConvertToViewModels(childs));
                Comments.Insert(0, new CommentViewModel(this, comment) { IsRoot = true });
            }
        }

        public class CommentViewModel : ViewModelBase
        {
            public RelayCommand NavigateCommand { get; set; }

            public bool IsRoot { get; set; }

            public CommentViewModel(PostViewModel parent, Comment comment)
            {
                NavigateCommand = new FixRelayCommand(() =>
                    parent.ChangeRootCommen(comment, IsRoot));
            }
        }
    }
}