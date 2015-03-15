using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Helper;

namespace JoyReactor.Core.ViewModels
{
    public class PostViewModel : ViewModelBase
    {
        public ObservableCollection<object> ViewModelParts { get; } = new ObservableCollection<object>();

        public ObservableCollection<RelatedPost> RelatedPost { get; } = new ObservableCollection<RelatedPost>();

        bool _isBusy;
        public bool IsBusy
        {
            get { return _isBusy; }
            set { Set(ref _isBusy, value); }
        }

        public RelayCommand OpenGalleryCommand { get; set; }

        IPostService postService;

        IDisposable postSubscription;
        IDisposable commentSubscription;

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

        public void Initialize(int postId)
        {
            postService = new PostService(postId);

            postSubscription?.Dispose();
            postSubscription = postService
                .Get()
                .SubscribeOnUi(post => {
                    ViewModelParts.ReplaceAt(0, post);
                    RelatedPost.ReplaceAll(post.RelatedPosts);
                });
            ReloadCommentList(0);
        }

        void ReloadCommentList(int commentId)
        {
            commentSubscription?.Dispose();
            commentSubscription = postService
                .Get(commentId)
                .SubscribeOnUi(comments =>
                {
                    ViewModelParts.ReplaceAll(1, new ViewModelBase[0]);
                    if (comments.Count >= 2 && comments[0].Id == comments[1].ParentCommentId)
                    {
                        ViewModelParts.Insert(1, new CommentViewModel(this, comments[0]) { IsRoot = true });
                        comments.RemoveAt(0);
                    }
                    ViewModelParts.AddRange(ConvertToViewModels(comments));
                });
        }

        IEnumerable<CommentViewModel> ConvertToViewModels(IEnumerable<Comment> comments)
        {
            foreach (var s in comments)
                yield return new CommentViewModel(this, s);
        }

        public override void Cleanup()
        {
            base.Cleanup();
            postSubscription?.Dispose();
            commentSubscription?.Dispose();
        }

        public class CommentViewModel : ViewModelBase
        {
            public RelayCommand NavigateCommand { get; set; }

            public bool IsRoot { get; set; }

            public string Text { get; set; }

            public int ChildCount { get; set; }

            public ICollection<string> Attachments { get; set; }

            public string UserImage { get; set; }

            public CommentViewModel() { }

            public CommentViewModel(PostViewModel parent, Comment comment)
            {
                UserImage = comment.UserImage;
                Attachments = comment.Attachments;

                Text = GetCommentText(comment);
                ChildCount = comment.ChildCount;
                NavigateCommand = new FixRelayCommand(() => parent.ReloadCommentList(IsRoot ? comment.ParentCommentId : comment.Id));
            }

            string GetCommentText(Comment comment)
            {
                return comment.Text;
            }
        }

        internal interface IPostService
        {
            IObservable<Post> Get();

            IObservable<List<Comment>> Get(int comment);
        }
    }
}