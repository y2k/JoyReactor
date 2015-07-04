using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model.Database;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;
using JoyReactor.Core.Model.Parser;
using JoyReactor.Core.ViewModels.Common;

namespace JoyReactor.Core.ViewModels
{
    public class PostViewModel : ScopedViewModel
    {
        public ObservableCollection<PostViewModel.CommentViewModel> Comments { get; }
            = new ObservableCollection<PostViewModel.CommentViewModel>();

        public ObservableCollection<string> CommentImages { get; }
            = new ObservableCollection<string>();

        public ObservableCollection<RelatedPost> RelatedPost { get; } = new ObservableCollection<RelatedPost>();

        public bool IsBusy { get { return Get<bool>(); } set { Set(value); } }

        public string Image { get { return Get<string>(); } set { Set(value); } }

        public float ImageAspect { get { return Get<float>(); } set { Set(value); } }

        public RelayCommand OpenGalleryCommand { get; set; }

        public ICommand OpenImageCommand { get; set; }

        int postId;

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
                Comments.AddRange(items);
            }
#endif
            OpenImageCommand = new Command(OpenImageCommandMethod);
        }

        public void OpenImageCommandMethod()
        {
            if (GalleryViewModel.IsCanShow(Image))
                BaseNavigationService.Instance.ImageFullscreen(Image);
        }

        public async Task Initialize(int postId)
        {
            this.postId = postId;

            await ReloadFromCache();
            await SyncWithWeb();
            await ReloadFromCache();
        }

        async Task ReloadFromCache()
        {
            var post = await new PostRepository().GetAsync(postId);
            Image = post.Image;
            ImageAspect = (float)post.ImageWidth / post.ImageHeight;
            var attachmentRepo = new AttachmentRepository();
            var images = (await attachmentRepo.GetAsync(postId))
                .Union(await attachmentRepo.GetForCommentsAsync(postId))
                .Select(s => s.Url)
                .Skip(1)
                .ToList();
            CommentImages.ReplaceAll(images);

            await ReloadCommentList(0);
        }

        async Task SyncWithWeb()
        {
            var post = await new PostRepository().GetAsync(postId);
            await new PostRequest(post.PostId).ComputeAsync();
        }

        async Task ReloadCommentList(int commentId)
        {
            var storage = new CommentRepository();
            var comments = await storage.GetChildCommentsAsync(postId, commentId);
            if (commentId != 0)
                comments.Insert(0, await storage.GetCommentAsync(commentId));
        
            Comments.Clear();
            var replies = false;
            if (comments.Count >= 2 && comments[0].Id == comments[1].ParentCommentId)
            {
                Comments.Insert(0, new CommentViewModel(this, comments[0]) { IsRoot = true });
                comments.RemoveAt(0);
                replies = true;
            }
            Comments.AddRange(ConvertToViewModels(comments, replies));
        }

        IEnumerable<CommentViewModel> ConvertToViewModels(List<Comment> comments, bool isReply)
        {
            foreach (var s in comments)
                yield return new CommentViewModel(this, s) { IsReply = isReply };
        }

        public class CommentViewModel : ViewModelBase
        {
            public RelayCommand NavigateCommand { get; set; }

            public bool IsRoot { get; set; }

            public bool IsReply { get; set; }

            public string Text { get; set; }

            public int ChildCount { get; set; }

            public ICollection<string> Attachments { get; set; }

            public string UserImage { get; set; }

            public float Rating { get; set; }

            public CommentViewModel()
            {
            }

            public CommentViewModel(PostViewModel parent, Comment comment)
            {
                UserImage = comment.UserImage;
                Attachments = comment.Attachments;

                Text = GetCommentText(comment);
                ChildCount = comment.ChildCount;
                Rating = comment.Rating;

                NavigateCommand = new Command(() => parent.ReloadCommentList(IsRoot ? comment.ParentCommentId : comment.Id));
            }

            string GetCommentText(Comment comment)
            {
                return comment.Text;
            }
        }
    }
}