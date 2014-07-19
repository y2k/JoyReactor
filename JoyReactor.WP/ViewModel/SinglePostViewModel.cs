using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.WP.Common;
using Microsoft.Practices.ServiceLocation;
using System.Collections.ObjectModel;

namespace JoyReactor.WP.ViewModel
{
    public class SinglePostViewModel : BaseViewModel
    {
        private IPostModel model = ServiceLocator.Current.GetInstance<IPostModel>();

        private string _imageUrl;
        public string ImageUrl { get { return _imageUrl; } set { Set(ref _imageUrl, value); } }

        private string _title;
        public string Title { get { return _title; } set { Set(ref _title, value); } }

        private string _content;
        public string Content { get { return _content; } set { Set(ref _content, value); } }

        public ObservableCollection<Comment> Comments { get; set; }
        public ObservableCollection<CommentAttachment> Attachments { get; set; }

        public SinglePostViewModel()
        {
            Comments = new ObservableCollection<Comment>();
            Attachments = new ObservableCollection<CommentAttachment>();
        }

        internal async void LoadData(int postId)
        {
            var p = await model.GetPostAsync(postId);
            ImageUrl = p.Image;
            Title = p.Title;

            Comments.Replace(await model.GetTopCommentsAsync(postId, 100));
            Attachments.Replace(await model.GetAttachmentsAsync(postId));
        }
    }
}