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
        public ObservableCollection<ViewModelBase> ViewModelParts { get; } = new ObservableCollection<ViewModelBase>();

        bool _isBusy;
        public bool IsBusy
        {
            get { return _isBusy; }
            set { Set(ref _isBusy, value); }
        }

        public RelayCommand OpenGalleryCommand { get; set; }

        public PostViewModel()
        {
#if DEBUG
            if (IsInDesignMode)
            {

                var items = Enumerable
                    .Range(1, 10)
                    .Select(s => Enumerable.Range(0, 2 * s))
                    .Select(s => s.Select(_ => "Тестовый текст - "))
                    .Select(s => s.Aggregate((a, b) => a + b))
                    .Select(s => new CommentViewModel { Text = s });
                ViewModelParts.AddRange(items);
            }
#endif
        }

        public async Task Initialize(int postId)
        {
            OpenGalleryCommand = new FixRelayCommand(() =>
                  MessengerInstance.Send(new GalleryNavigationMessage { PostId = postId }));

            IsBusy = true;
            ViewModelParts.Clear();

            var post = await new PostModel().GetPostAsync(postId);
            var attachments = await new PostModel().GetPostAttachmentsAsync(postId);

            var poster = attachments.Select(s => s.PreviewImageUrl).FirstOrDefault();
            ViewModelParts.Add(new PosterViewModel { Image = poster });
            ViewModelParts.AddRange(ConvertToViewModels(await new PostModel().GetChildCommentsAsync(postId, 0)));

            IsBusy = false;
        }

        IEnumerable<CommentViewModel> ConvertToViewModels(IEnumerable<CommentWithChildCount> comments)
        {
            foreach (var s in comments)
                yield return new CommentViewModel(this, s);
        }

        async void ChangeRootComment(CommentWithChildCount comment, bool isRoot)
        {
            if (isRoot)
            {
                var comments = await new PostModel().GetCommentsWithSameParentAsync(comment.PostId, comment.Id);
                var parent = await new PostModel().GetParentCommentAsync(comment.PostId, comment.Id);
                ViewModelParts.ReplaceAll(1, ConvertToViewModels(comments));
                if (parent != null)
                    ViewModelParts.Insert(1, new CommentViewModel(this, parent) { IsRoot = true });
            }
            else
            {
                var childs = await new PostModel().GetChildCommentsAsync(comment.PostId, comment.Id);
                ViewModelParts.ReplaceAll(1, ConvertToViewModels(childs));
                ViewModelParts.Insert(1, new CommentViewModel(this, comment) { IsRoot = true });
            }
        }

        public class PosterViewModel : ViewModelBase
        {
            string _image;
            public string Image
            {
                get { return _image; }
                set { Set(ref _image, value); }
            }
        }

        public class CommentViewModel : ViewModelBase
        {
            public RelayCommand NavigateCommand { get; set; }

            public bool IsRoot { get; set; }

            public string Text { get; set; }

            public int ChildCount { get; set; }

            public ICollection<string> Images { get; set; }

            public CommentViewModel() { }

            public CommentViewModel(PostViewModel parent, CommentWithChildCount comment)
            {
                Text = comment.Text;
                ChildCount = comment.ChildCount;
                NavigateCommand = new FixRelayCommand(() =>
                   parent.ChangeRootComment(comment, IsRoot));
            }
        }
    }
}